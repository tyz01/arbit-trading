package io.app.arbittrading.binance.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class BinanceTickerPrice {
    private String symbol;
    private BigDecimal price;
}
