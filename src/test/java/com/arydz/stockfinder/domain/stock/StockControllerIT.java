package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.BaseIntegrationTest;
import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.dictionary.model.MarketIndexEntity;
import com.arydz.stockfinder.domain.file.ExtractionMode;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class StockControllerIT extends BaseIntegrationTest {

    @LocalServerPort
    private String port;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StockRepository repository;

    @Test
    void shouldReturnStockList() throws JsonProcessingException {

        // given
        String url = String.format(WEB_URL_PATTERN, port, "/api/stock/?page=0&size=20&sortColumn=id&sortDirection=ASC");

        // when
        FluxExchangeResult<String> result = this.webClient
                .get()
                .uri(url)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .returnResult(String.class);

        // then
        HttpStatus httpStatus = result.getStatus();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        String jsonResponse = result.getResponseBody().blockFirst();
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
                        .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody(externalServerJsonResponse)
                ));
        String url = String.format(WEB_URL_PATTERN, port, "/api/stock/import");

        // when
        FluxExchangeResult<String> result = this.webClient
                .post()
                .uri(url)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .returnResult(String.class);

        // then
        HttpStatus httpStatus = result.getStatus();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);
        String jsonResponse = result.getResponseBody().blockFirst();
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

    @Test
    void shouldUpdateStockMarketIndexInDatabase() {

        // given
        String url = String.format(WEB_URL_PATTERN, port, "/api/stock/update/market");
        File testDataZip = getTestDataZip();
        BodyInserters.MultipartInserter multipartInserter = fromFile(testDataZip);

        // when
        FluxExchangeResult<String> result = this.webClient
                .put()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .body(multipartInserter)
                .exchange()
                .returnResult(String.class);

        // then
        HttpStatus httpStatus = result.getStatus();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        List<StockEntity> stockEntityList = repository.findAll();
        Optional<StockEntity> optionalStockEntity = stockEntityList.stream()
                .filter(s -> s.getTicker().equalsIgnoreCase("ST1"))
                .findAny();

        assertThat(optionalStockEntity)
                .isPresent()
                .map(StockEntity::getMarketIndexEntity)
                .isPresent()
                .map(MarketIndexEntity::getName)
                .hasValue("NASDAQ");
    }

    private File getTestDataZip() {
        return new File(
                this.getClass()
                        .getClassLoader()
                        .getResource("files/test_data.zip")
                        .getFile());
    }


    public BodyInserters.MultipartInserter fromFile(File file) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file));
        builder.part("chartTimeframeType", ChartTimeframeType.DAILY);
        builder.part("extractionMode", ExtractionMode.EXCLUDE_JSON_FOLDERS);

        return BodyInserters.fromMultipartData(builder.build());
    }

}