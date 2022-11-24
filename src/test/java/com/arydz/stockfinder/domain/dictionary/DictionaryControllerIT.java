package com.arydz.stockfinder.domain.dictionary;

import com.arydz.stockfinder.BaseIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DictionaryControllerIT extends BaseIntegrationTest {

    @LocalServerPort
    private String port;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnMarketIndexNameList() throws JsonProcessingException {

        // given
        String url = String.format(WEB_URL_PATTERN, port, "/api/dictionary/marketIndexName");

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
        List<String> marketIndexNameList = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertThat(marketIndexNameList).hasSize(3);
    }
}