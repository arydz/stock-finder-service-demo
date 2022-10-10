package com.arydz.stockfinder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile(value = "!test")
public class WebClientConfig {

    @Value("${edgar.sec.base.url}")
    private String edgarBaseUrl;

    @Value("${sf.api.file.max-in-memory-size.mb}")
    private int maxInMemorySizeMb;

    @Bean(name = "edgarWebClient")
    public WebClient edgarWebClient() {

        ExchangeStrategies exchangeStrategy = getExchangeStrategies();

        return WebClient.builder()
                .exchangeStrategies(exchangeStrategy)
                .baseUrl(edgarBaseUrl)
                .build();
    }

    public ExchangeStrategies getExchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(maxInMemorySizeMb * 1024 * 1024))
                .build();
    }

}
