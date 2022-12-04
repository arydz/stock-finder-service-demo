package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.domain.file.ExtractionMode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/chart")
@AllArgsConstructor
public class ChartController {

    private final ChartService chartService;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> manuallyImport(@RequestPart("file") FilePart file,
                                           @RequestPart("chartTimeframeType") ChartTimeframeType chartTimeframeType,
                                           @RequestPart("extractionMode") ExtractionMode extractionMode) {

        return chartService.importChartData(file, chartTimeframeType, extractionMode);
    }
}
