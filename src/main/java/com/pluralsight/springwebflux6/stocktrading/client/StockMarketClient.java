package com.pluralsight.springwebflux6.stocktrading.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.pluralsight.springwebflux6.stockmarket.api.currencyrate.CurrencyRate;
import com.pluralsight.springwebflux6.stockmarket.api.stockpublish.StockPublishRequest;
import com.pluralsight.springwebflux6.stockmarket.api.stockpublish.StockPublishResponse;
import com.pluralsight.springwebflux6.stocktrading.exception.StockCreationException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class StockMarketClient {
    
    private WebClient webClient;

    public StockMarketClient(@Value("${clients.stockMarket.baseUrl}") 
                                String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        request -> Mono.just(ClientRequest.from(request)
                                .header("X-Trace-Id", UUID.randomUUID().toString())
                                .build())
                        ))
                .build();
    }
    
    public Flux<CurrencyRate> getCurrencyRates() {
        return webClient.get()
                .uri("/currencyRates")
                .retrieve()
                .bodyToFlux(CurrencyRate.class)
                .doFirst(() -> log.info("Calling GET Currency Rates API"))
                .doOnNext(cr -> log.info("GET Currency Rates API Response: {}", cr));
    }

    public Mono<StockPublishResponse> publishStock(
                                        StockPublishRequest requestBody) {
        return webClient.post()
            .uri("/stocks/publish")
            .body(BodyInserters.fromValue(requestBody))
            .exchangeToMono(response ->
                    !response.statusCode().isError() ?
                        response.bodyToMono(StockPublishResponse.class) :
                        response.bodyToMono(ProblemDetail.class)
                                .flatMap(errorMessage -> 
                                        Mono.error(new StockCreationException(errorMessage.getDetail()))))
            .doFirst(() -> log.info("Calling Publish Stock API with Request Body: {}", requestBody))
            .doOnNext(spr -> log.info("Publish Stock API Response: {}", spr));
    }
}
