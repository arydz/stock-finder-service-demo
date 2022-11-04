package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.common.EdgarClient;
import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.model.EdgarStock;
import com.arydz.stockfinder.domain.stock.model.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class StockService {

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

    public Mono<Page<Stock>> findAll(int page, int size) {

        log.info("About to find all stocks for page {} and size of page {}", page, size);
        return Mono.just(PageRequest.of(page, size))
                .map(repository::findAll)
                .map(entityPages -> entityPages.map(stockMapper::mapEntityToStock));
    }
}
