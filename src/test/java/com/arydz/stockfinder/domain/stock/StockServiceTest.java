package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.common.EdgarClient;
import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.model.EdgarStock;
import com.arydz.stockfinder.domain.stock.model.Stock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.just;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    private EdgarClient edgarClient;
    private StockMapper stockMapper;
    private StockRepository repository;

    private StockService stockService;

    @BeforeEach
    void setUp() {

        edgarClient = mock(EdgarClient.class);
        stockMapper = mock(StockMapper.class);
        StockDao dao = mock(StockDao.class);
        repository = mock(StockRepository.class);
        stockService = new StockService(edgarClient, stockMapper, dao, repository);
    }

    @Test
    void verifyImportingStocks() {

        // given
        List<EdgarStock> edgarStockList = List.of(EdgarStock.builder().ticker("ST1").title("Stock 1").cik(1).build());
        when(edgarClient.getEdgarCompanyTickers()).thenReturn(just(edgarStockList));

        // when
        Mono<String> response = stockService.importStocks();

        // then
        StepVerifier.create(response)
                .expectNext("1 stocks imported successfully")
                .verifyComplete();
    }

    @Test
    void verifyFindingAllStocks() {

        // given
        List<StockEntity> edgarStockList = List.of(new StockEntity(1L, "ST1", "Stock 1", 1, null));
        PageImpl<StockEntity> stockEntityPage = new PageImpl<>(edgarStockList);
        when(repository.findAll(any(Pageable.class))).thenReturn(stockEntityPage);
        Stock expectedStock = Stock.builder().id(1L).ticker("ST1").title("Stock 1").build();
        when(stockMapper.mapEntityToStock(any())).thenReturn(expectedStock);
        // when
        Mono<Page<Stock>> response = stockService.findAll(0, 1);

        // then
        StepVerifier.create(response)
                .assertNext(stockPage -> {
                    Assertions.assertThat(stockPage).isNotEmpty();
                    long totalElements = stockPage.getTotalElements();
                    Assertions.assertThat(totalElements).isEqualTo(1);
                    List<Stock> content = stockPage.getContent();
                    Assertions.assertThat(content).containsOnly(expectedStock);
                })
                .verifyComplete();

    }
}