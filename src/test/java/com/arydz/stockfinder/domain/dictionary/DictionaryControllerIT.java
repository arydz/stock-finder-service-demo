package com.arydz.stockfinder.domain.dictionary;

import com.arydz.stockfinder.BaseIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DictionaryControllerIT extends BaseIntegrationTest {

    @LocalServerPort
    private String port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnMarketIndexNameList() throws JsonProcessingException {

        // given
        String url = String.format("http://localhost:%s%s", port, "/api/dictionary/marketIndexName");

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String jsonResponse = response.getBody();
        assertThat(jsonResponse).isNotNull();
        List<String> marketIndexNameList = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertThat(marketIndexNameList).hasSize(3);
    }
}