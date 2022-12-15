package com.arydz.stockfinder.domain.dictionary;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.file.ExtractionMode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dictionary")
@AllArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @GetMapping("/marketIndexName")
    public Mono<List<String>> getMarketIndexList() {
        return dictionaryService.getMarketIndexNameList();
    }

    @GetMapping("/chartTimeframeType")
    public Mono<ChartTimeframeType[]> getChartTimeframeTypeNameList() {

        return dictionaryService.getChartTimeframeTypes();
    }

    @GetMapping("/extractionMode")
    public Mono<ExtractionMode[]> getExtractionModeNameList() {

        return dictionaryService.getExtractionModes();
    }
}
