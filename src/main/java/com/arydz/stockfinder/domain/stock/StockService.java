package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.common.EdgarClient;
import com.arydz.stockfinder.domain.dictionary.DictionaryService;
import com.arydz.stockfinder.domain.dictionary.model.MarketIndex;
import com.arydz.stockfinder.domain.file.ExtractionMode;
import com.arydz.stockfinder.domain.file.FileService;
import com.arydz.stockfinder.domain.stock.api.FilterStockParams;
import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.model.EdgarStock;
import com.arydz.stockfinder.domain.stock.model.SimpleStock;
import com.arydz.stockfinder.domain.stock.model.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private static final String IMPORT_STOCKS_MESSAGE = "%s stocks imported successfully";

    private final EdgarClient edgarClient;
    private final StockMapper stockMapper;
    private final StockDao dao;
    private final StockRepository repository;
    private final DictionaryService dictionaryService;
    private final FileService fileService;

    public Mono<String> importStocks() {

        return edgarClient.getEdgarCompanyTickers()
                .map(this::saveStockList)
                .map(this::prepareImportStockMessage);
    }

    private int saveStockList(List<EdgarStock> edgarStocks) {

        log.info("About to save tickers");

        List<StockEntity> entityList = edgarStocks.stream()
                .map(stockMapper::mapEdgarStockToEntity)
                .collect(Collectors.toList());

        dao.saveAll(entityList);

        int size = entityList.size();
        log.info("Saved {} tickers", size);
        return size;
    }

    private String prepareImportStockMessage(Integer size) {
        return String.format(IMPORT_STOCKS_MESSAGE, size);
    }

    public Mono<Page<Stock>> findAll(FilterStockParams params) {

        int page = params.getPage();
        int size = params.getSize();
        Sort sort = Sort.by(params.getSortDirection(), params.getSortColumn());

        log.info("About to find all stocks for page {}, size of page {}, sort by {} and direction {}", page, size, params.getSortColumn(), params.getSortDirection());
        return Mono.just(PageRequest.of(page, size, sort))
                .map(pageRequest -> {
                    String ticker = params.getTicker();
                    String title = params.getTitle();
                    return repository.findAll(ticker, title, pageRequest);
                })
                .map(entityPages -> entityPages.map(stockMapper::mapEntityToStock));
    }

    public List<SimpleStock> getSimpleStockList() {

        return repository.findAllSimpleStocks();
    }

    public Mono<Void> updateMarketIndex(FilePart file, ChartTimeframeType chartTimeframeType, ExtractionMode extractionMode) {

        Mono<Path> uploadFileMono = fileService.uploadZipFile(Mono.just(file));

        return Mono.zip(uploadFileMono, Mono.just(chartTimeframeType), Mono.just(extractionMode))
                .flatMap(fileService::extractZipFile)
                .flatMap(paths -> {
                    Path zipExtractionPath = paths.getT2();
                    return fileService.performExtractedFiles(zipExtractionPath, this::updateMarketIndex);
                })
                .doFinally(signalType -> fileService.deleteTemporaryFiles())
                .then();
    }

    private void updateMarketIndex(Set<Path> importableFolderList) {

        List<SimpleStock> simpleStockList = getSimpleStockList();
        dictionaryService.getMarketIndexList().flatMap(marketIndexList -> {

            for (Path folderPath : importableFolderList) {
                tryUpdateMarketIndex(simpleStockList, marketIndexList, folderPath);
            }

            return Mono.just(HttpStatus.OK);
        }).subscribe();
    }

    private void tryUpdateMarketIndex(List<SimpleStock> simpleStockList, List<MarketIndex> marketIndexList, Path folderPath) {
        try (Stream<Path> paths = Files.list(folderPath)) {

            List<Path> pathList = paths.collect(Collectors.toList());
            for (MarketIndex marketIndex : marketIndexList) {

                for (Path path : pathList) {
                    String pathString = path.toString();
                    String marketIndexName = marketIndex.getName();
                    if (StringUtils.containsIgnoreCase(pathString, marketIndexName)) {

                        String currentTicker = FilenameUtils.getBaseName(path.getFileName().toString());
                        simpleStockList.stream()
                                .filter(stock -> stock.getTicker().equalsIgnoreCase(currentTicker))
                                .findAny()
                                .ifPresent(stock -> {
                                    Long stockId = stock.getId();
                                    Long marketIndexId = marketIndex.getId();
                                    repository.updateMarketIndex(stockId, marketIndexId);
                                });
                    }
                }
            }

        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Can't list files in directory %s. Details: %s", folderPath, e.getMessage()), e);
        }
    }

}
