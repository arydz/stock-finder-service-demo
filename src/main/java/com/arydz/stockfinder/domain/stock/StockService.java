package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.common.EdgarClient;
import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.model.EdgarStock;
import com.arydz.stockfinder.domain.stock.model.FilterStockParams;
import com.arydz.stockfinder.domain.stock.model.SimpleStock;
import com.arydz.stockfinder.domain.stock.model.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private static final String IMPORT_STOCKS_MESSAGE = "%s stocks imported successfully";

    private final EdgarClient edgarClient;
    private final StockMapper stockMapper;
    private final StockDao dao;
    private final StockRepository repository;

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
                .map( pageRequest -> {
                    String ticker = params.getTicker();
                    String title = params.getTitle();
                    return repository.findAll(ticker, title, pageRequest);
                })
                .map(entityPages -> entityPages.map(stockMapper::mapEntityToStock));
    }

    public List<SimpleStock> getSimpleStockList() {

        return repository.findAllSimpleStocks();
    }
}
