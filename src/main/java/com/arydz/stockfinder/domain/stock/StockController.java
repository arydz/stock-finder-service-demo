package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.stock.model.FilterStockParams;
import com.arydz.stockfinder.domain.stock.model.Stock;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;

    @PostMapping
    @Operation(summary = "Import company data from external vendor")
    public Mono<String> importStocks() {
        return service.importStocks();
    }

    @GetMapping
    @Operation(summary = "Get company data in pages, stored in a database")
    public Mono<Page<Stock>> getAllStocks(FilterStockParams params) {

        return service.findAll(params);
    }

}
