package com.arydz.stockfinder.domain.stock;

import com.arydz.stockfinder.domain.stock.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;

    @PostMapping
    public Mono<String> importStocks() {
        return service.importStocks();
    }

    @GetMapping
    public Mono<Page<Stock>> getAllStocks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {

        return service.findAll(page, size);
    }

}
