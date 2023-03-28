package io.app.arbittrading.binance.service.impl;

import io.app.arbittrading.binance.client.BinanceFeignClient;
import io.app.arbittrading.binance.model.BinanceTickerPrice;
import io.app.arbittrading.binance.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BinanceServiceImpl implements BinanceService {
    private BinanceFeignClient binanceFeignClient;

    @Autowired
    public BinanceServiceImpl(BinanceFeignClient binanceFeignClient) {
        this.binanceFeignClient = binanceFeignClient;
    }
    @Override
    public List<BinanceTickerPrice> getTickerPrices() {
        return binanceFeignClient.getTickerPrices();
    }
}