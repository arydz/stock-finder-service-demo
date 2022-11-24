package com.arydz.stockfinder.config;

import com.arydz.stockfinder.domain.common.EnvProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@Profile(value = "!test")
public class WebClientConfig implements WebFluxConfigurer {

    private final EnvProperties properties;

    @Bean(name = "edgarWebClient")
    public WebClient edgarWebClient() {

        ExchangeStrategies exchangeStrategy = getExchangeStrategies();

        return WebClient.builder()
                .exchangeStrategies(exchangeStrategy)
                .baseUrl(properties.getEdgarBaseUrl())
                .build();
    }

    public ExchangeStrategies getExchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(properties.getMaxInMemorySizeMb() * 1024 * 1024))
                .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
                .allowedOrigins(properties.getAllowedOrigins());
    }
}
