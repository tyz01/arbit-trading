package io.app.arbittrading.binance.service;

import io.app.arbittrading.binance.model.BinanceTickerPrice;

import java.util.List;

public interface BinanceService {
    List<BinanceTickerPrice> getTickerPrices();
}
