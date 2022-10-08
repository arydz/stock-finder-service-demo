package com.arydz.stockfinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile(value = "test")
public class TestConfig {

    @Bean(name = "wireMockServer")
    public WireMockServer wireMockServer() {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        return wireMockServer;
    }

    @Bean(name = "edgarWebClient")
    public WebClient edgarWebClient(WireMockServer wireMockServer) {
        return WebClient.builder().baseUrl(wireMockServer.baseUrl()).build();
    }

    @Bean(name = "objectMapper")
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.registerModule(new PageJacksonModule()).registerModule(new SortJacksonModule());
    }
}
