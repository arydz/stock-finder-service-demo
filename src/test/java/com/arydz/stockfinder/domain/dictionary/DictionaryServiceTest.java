package com.arydz.stockfinder.domain.dictionary;

import com.arydz.stockfinder.domain.dictionary.model.MarketIndex;
import com.arydz.stockfinder.domain.dictionary.model.MarketIndexEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictionaryServiceTest {

    private MarketIndexMapper marketIndexMapper;
    private MarketIndexRepository repository;

    private DictionaryService dictionaryService;

    @BeforeEach
    void setUp() {

        marketIndexMapper = mock(MarketIndexMapper.class);
        repository = mock(MarketIndexRepository.class);
        dictionaryService = new DictionaryService(repository, marketIndexMapper);
    }

    @Test
    void verifyImportingStocks() {

        // given
        String marketName = "Market";
        List<String> expected = List.of(marketName);

        MarketIndexEntity marketIndexEntity = getDummyEntity(marketName);
        List<MarketIndexEntity> marketIndexEntityList = List.of(marketIndexEntity);
        when(repository.findAll()).thenReturn(marketIndexEntityList);
        MarketIndex marketIndex = getDummyModel(marketName);
        when(marketIndexMapper.mapToMarketIndex(any(MarketIndexEntity.class))).thenReturn(marketIndex);

        // when
        Mono<List<String>> response = dictionaryService.getMarketIndexNameList();

        // then
        StepVerifier.create(response)
                .expectNext(expected)
                .verifyComplete();
    }

    private static MarketIndexEntity getDummyEntity(String marketName) {
        MarketIndexEntity marketIndexEntity = new MarketIndexEntity();
        marketIndexEntity.setId(1L);
        marketIndexEntity.setName(marketName);
        marketIndexEntity.setCountry("USA");
        return marketIndexEntity;
    }

    private static MarketIndex getDummyModel(String marketName) {
        MarketIndex marketIndex = new MarketIndex();
        marketIndex.setId(1L);
        marketIndex.setName(marketName);
        marketIndex.setCountry("USA");
        return marketIndex;
    }
}