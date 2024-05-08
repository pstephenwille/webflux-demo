package com.pluralsight.springwebflux6.stocktrading.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.pluralsight.springwebflux6.stockmarket.api.currencyrate.CurrencyRate;
import com.pluralsight.springwebflux6.stocktrading.client.StockMarketClient;
import com.pluralsight.springwebflux6.stocktrading.dto.StockResponse;
import com.pluralsight.springwebflux6.stocktrading.model.Stock;
import com.pluralsight.springwebflux6.stocktrading.repository.StocksRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StockIT {

    private static final String STOCK_ID = "621a97f1d11fc40fcdd5c67b";
    private static final String STOCK_NAME = "Globomantics";
    private static final BigDecimal STOCK_PRICE = BigDecimal.TEN;
    private static final String STOCK_CURRENCY = "USD";

    @MockBean
    StocksRepository stocksRepository;

    @MockBean
    StockMarketClient stockMarketClient;

    @Autowired
    WebTestClient client;

    @Test
    void shouldGetOneStock() {
        // GIVEN
        Stock stock = Stock.builder()
                .id(STOCK_ID)
                .name(STOCK_NAME)
                .price(STOCK_PRICE)
                .currency(STOCK_CURRENCY)
                .build();
        CurrencyRate currencyRate = CurrencyRate.builder()
                .currencyName("USD")
                .rate(BigDecimal.ONE)
                .build();
        when(stocksRepository.findById(STOCK_ID))
                .thenReturn(Mono.just(stock));
        when(stockMarketClient.getCurrencyRates())
                .thenReturn(Flux.just(currencyRate));
        // WHEN
        StockResponse stockResponse = client.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/stocks/{id}")
                                .build(STOCK_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockResponse.class)
                .returnResult()
                .getResponseBody();
        // THEN
        assertNotNull(stockResponse);
        assertEquals(stockResponse.getId(), STOCK_ID);
        assertEquals(stockResponse.getName(), STOCK_NAME);
        assertEquals(stockResponse.getPrice(), STOCK_PRICE);
        assertEquals(stockResponse.getCurrency(), STOCK_CURRENCY);
    }

    @Test
    void shouldReturnNotFoundWhenGetOneStock() {
        // GIVEN
        when(stocksRepository.findById(STOCK_ID))
                .thenReturn(Mono.empty());
        // WHEN
        ProblemDetail problemDetail = client.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/stocks/{id}")
                                .build(STOCK_ID))
                .exchange()
        // THEN
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertTrue(problemDetail.getDetail().contains("Stock not found"));

    }
    
}
