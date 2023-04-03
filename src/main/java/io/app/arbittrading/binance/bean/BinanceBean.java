package io.app.arbittrading.binance.bean;

import java.math.BigDecimal;
public record BinanceBean(String symbol, BigDecimal price) {
}
