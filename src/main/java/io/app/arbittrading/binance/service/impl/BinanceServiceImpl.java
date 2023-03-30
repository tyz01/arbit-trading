package io.app.arbittrading.binance.service.impl;

import io.app.arbittrading.binance.bean.BinanceBean;
import io.app.arbittrading.binance.client.BinanceFeignClient;
import io.app.arbittrading.binance.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BinanceServiceImpl implements BinanceService {
    private final BinanceFeignClient binanceFeignClient;
    @Override
    public List<BinanceBean> getTickerPrices() {
        try {
            return binanceFeignClient.getTickerPrices();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}