package com.arydz.stockfinder.domain.common;

import com.arydz.stockfinder.domain.stock.model.EdgarStock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class EdgarClient {

    private static final String ERROR_MESSAGE = "Couldn't fetch data from Edgar server. Details: %s";
    private static final ParameterizedTypeReference<Map<String, EdgarStock>> MAP_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };

    @Value("${edgar.sec.files.company.tickers.url}")
    private String edgarTickersUrl;

    private final WebClient edgarWebClient;

    public Mono<List<EdgarStock>> getEdgarCompanyTickers() {
        log.info("About to get Edgar company tickers");
        return edgarWebClient.get()
                .uri(edgarTickersUrl)
                .retrieve()
                .bodyToMono(MAP_TYPE_REFERENCE)
                .onErrorMap(throwable -> new IllegalArgumentException(format(ERROR_MESSAGE, throwable.getMessage()), throwable))
                .map(jsonMap -> new ArrayList<>(jsonMap.values()));
    }
}
