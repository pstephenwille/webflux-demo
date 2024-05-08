package com.pluralsight.springwebflux6.stocktrading.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.pluralsight.springwebflux6.stocktrading.model.Stock;

@Repository
public interface StocksRepository extends ReactiveMongoRepository<Stock, String> {
}
