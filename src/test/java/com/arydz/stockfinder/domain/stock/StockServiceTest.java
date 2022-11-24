package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.common.EdgarClient;
import com.arydz.stockfinder.domain.dictionary.DictionaryService;
import com.arydz.stockfinder.domain.file.FileService;
import com.arydz.stockfinder.domain.stock.api.FilterStockParams;
import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.model.EdgarStock;
import com.arydz.stockfinder.domain.stock.model.Stock;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        DictionaryService dictionaryService = mock(DictionaryService.class);
        FileService fileService = mock(FileService.class);
        stockService = new StockService(edgarClient, stockMapper, dao, repository, dictionaryService, fileService);
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
        when(repository.findAll(anyString(), anyString(), any(Pageable.class))).thenReturn(stockEntityPage);
        Stock expectedStock = Stock.builder().id(1L).ticker("ST1").title("Stock 1").build();
        when(stockMapper.mapEntityToStock(any())).thenReturn(expectedStock);
        FilterStockParams params = new FilterStockParams(StringUtils.EMPTY, StringUtils.EMPTY, 0, 1, "id", Sort.Direction.ASC);

        // when
        Mono<Page<Stock>> response = stockService.findAll(params);

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