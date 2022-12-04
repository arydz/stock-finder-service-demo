package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.BaseIntegrationTest;
import com.arydz.stockfinder.domain.chart.db.ChartDailyEntity;
import com.arydz.stockfinder.domain.file.ExtractionMode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.util.List;

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
        BodyInserters.MultipartInserter multipartInserter = fromFile(testDataZip);

        // when
        FluxExchangeResult<String> result = this.webClient
                .post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .body(multipartInserter)
                .exchange()
                .returnResult(String.class);

        // then
        HttpStatus httpStatus = result.getStatus();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        List<ChartDailyEntity> dailyEntityList = dailyRepository.findAll();

        assertThat(dailyEntityList)
                .isNotEmpty()
                .hasSize(2);
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