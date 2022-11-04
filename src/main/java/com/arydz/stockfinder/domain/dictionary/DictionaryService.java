package com.arydz.stockfinder.domain.dictionary;

import com.arydz.stockfinder.domain.dictionary.model.MarketIndex;
import com.arydz.stockfinder.domain.dictionary.model.MarketIndexEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DictionaryService {

    private final MarketIndexRepository marketIndexRepository;
    private final MarketIndexMapper marketIndexMapper;

    public Mono<List<String>> getMarketIndexNameList() {

        return Mono.just(marketIndexRepository.findAll())
                .map(this::mapToMarketIndex);
    }

    private List<String> mapToMarketIndex(List<MarketIndexEntity> entityList) {

        return entityList.stream()
                .map(marketIndexMapper::mapToMarketIndex)
                .sorted(this::compareByNameLength)
                .map(MarketIndex::getName)
                .collect(Collectors.toList());
    }

    private int compareByNameLength(MarketIndex o1, MarketIndex o2) {

        int length1 = o1.getName().length();
        int length2 = o2.getName().length();
        return length2 - length1;
    }

}
