package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.BaseIntegrationTest;
import com.arydz.stockfinder.domain.chart.db.ChartDailyEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.util.List;

import static com.arydz.stockfinder.TestUtilities.getMultipartInserter;
import static com.arydz.stockfinder.TestUtilities.getTestDataZip;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ChartControllerIT extends BaseIntegrationTest {

    @LocalServerPort
    private String port;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ChartRepository<ChartDailyEntity> dailyRepository;

    @Test
    void shouldUpdateStockMarketIndexInDatabase() {

        // given
        String url = String.format(WEB_URL_PATTERN, port, "/api/chart/import");
        File testDataZip = getTestDataZip();
        BodyInserters.MultipartInserter multipartInserter = getMultipartInserter(testDataZip);

        // when
        WebTestClient.ResponseSpec responseSpec = this.webClient
                .post()
                .uri(url)
                .body(multipartInserter)
                .exchange();

        // then
        responseSpec.expectStatus()
                .is2xxSuccessful()
                .expectBody(Long.class)
                .isEqualTo(1L);

        List<ChartDailyEntity> dailyEntityList = dailyRepository.findAll();

        assertThat(dailyEntityList)
                .isNotEmpty()
                .hasSize(2);
    }
}