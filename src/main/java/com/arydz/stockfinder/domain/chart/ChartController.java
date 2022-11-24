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
    public Mono<Long> manuallyImport(@RequestPart("file") FilePart file,
                                     @RequestPart("chartTimeframeType") String chartTimeframeTypeName,
                                     @RequestPart("extractionMode") String extractionModeName) {

        ChartTimeframeType chartTimeframeType = ChartTimeframeType.valueOf(chartTimeframeTypeName);
        ExtractionMode extractionMode = ExtractionMode.valueOf(extractionModeName);

        return chartService.importChartData(file, chartTimeframeType, extractionMode);
    }
}
