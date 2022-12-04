package com.arydz.stockfinder.domain.stock.api;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.file.ExtractionMode;
import com.arydz.stockfinder.domain.stock.StockService;
import com.arydz.stockfinder.domain.stock.model.Stock;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;

    @PostMapping(value = "/import")
    @Operation(summary = "Import company data from external vendor")
    public Mono<String> importStocks() {
        return service.importStocks();
    }

    @GetMapping
    @Operation(summary = "Get company data in pages, stored in a database")
    public Mono<Page<Stock>> getAllStocks(FilterStockParams params) {

        return service.findAll(params);
    }

    @PutMapping(value = "/update/market", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> manuallyImport(@RequestPart("file") FilePart file,
                                     @RequestPart("chartTimeframeType") ChartTimeframeType chartTimeframeType,
                                     @RequestPart("extractionMode") ExtractionMode extractionMode) {

        return service.updateMarketIndex(file, chartTimeframeType, extractionMode);
    }
}
