package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.BaseIntegrationTest;
import com.arydz.stockfinder.domain.stock.db.StockEntity;
import com.arydz.stockfinder.domain.stock.model.Stock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class StockControllerIT extends BaseIntegrationTest {

    @LocalServerPort
    private String port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StockRepository repository;

    @Test
    void shouldReturnStockList() throws JsonProcessingException, InterruptedException {

        // given
        String url = String.format("http://localhost:%s%s", port, "/api/stock?page=0&size=20");

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String jsonResponse = response.getBody();
        assertThat(jsonResponse).isNotNull();
        Page<Stock> stockPage = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertThat(stockPage.getTotalElements()).isEqualTo(2L);
    }

    @ParameterizedTest
    @MethodSource("provideStockTickers")
    void shouldImportStocksIntoDbFromWeb(String externalServerJsonResponse, int expectedStocksInDb) {

        // given
        wireMockServer.stubFor(get(urlEqualTo("/files/tickers.json"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(externalServerJsonResponse)
                ));
        String url = String.format("http://localhost:%s%s", port, "/api/stock");

        // when
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String jsonResponse = response.getBody();
        assertThat(jsonResponse).isNotNull();
        List<StockEntity> stockEntityList = repository.findAll();
        assertThat(stockEntityList).hasSize(expectedStocksInDb);
    }

    private static Stream<Arguments> provideStockTickers() {
        return Stream.of(
                // ST3 is new stock, ST1 already exist in DB
                Arguments.of("{\"0\":{\"cik_str\":1,\"ticker\":\"ST1\",\"title\":\"Stock 1\"},\"1\":{\"cik_str\":2,\"ticker\":\"ST3\",\"title\":\"Stock 3\"}}", 3),
                // ST1 & ST2 already exist in DB
                Arguments.of("{\"0\":{\"cik_str\":1,\"ticker\":\"ST1\",\"title\":\"Stock 1\"},\"1\":{\"cik_str\":2,\"ticker\":\"ST2\",\"title\":\"Stock 2\"}}", 2),
                // Nothing to add, ST1 & ST2 already exist in DB
                Arguments.of("{}", 2)
        );
    }
}