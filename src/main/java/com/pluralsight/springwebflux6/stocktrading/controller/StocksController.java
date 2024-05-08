package com.pluralsight.springwebflux6.stocktrading.controller;


import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.*;

import com.pluralsight.springwebflux6.stocktrading.dto.StockRequest;
import com.pluralsight.springwebflux6.stocktrading.dto.StockResponse;
import com.pluralsight.springwebflux6.stocktrading.service.StocksService;

@AllArgsConstructor
@RestController
@RequestMapping("/stocks")
public class StocksController {

    private StocksService stocksService;

    @GetMapping("/{id}")
    public Mono<StockResponse> getOneStock(@PathVariable String id, 
            @RequestParam(value = "currency", defaultValue = "USD") 
                        String currency) {
        return stocksService.getOneStock(id, currency);
    }

    @GetMapping
    public Flux<StockResponse> getAllStocks(
        @RequestParam(required = false, defaultValue = "0") 
            BigDecimal priceGreaterThan) {
        return stocksService.getAllStocks(priceGreaterThan);
    }

    @PostMapping
    public Mono<StockResponse> createStock(@RequestBody StockRequest stock) {
        return stocksService.createStock(stock);
    }

}
