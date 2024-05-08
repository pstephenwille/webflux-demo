package com.pluralsight.springwebflux6.stocktrading.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Stock {

    @Id
    private String id;

    private String name;

    @NonNull
    private BigDecimal price;

    private String currency;

}
