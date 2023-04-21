package io.app.arbittrading.binance.service.impl;

import io.app.arbittrading.binance.bean.BinanceBean;
import io.app.arbittrading.binance.client.BinanceFeignClient;
import io.app.arbittrading.binance.service.BinanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BinanceServiceImpl implements BinanceService {
    private final BinanceFeignClient binanceFeignClient;
    @Override
    public List<BinanceBean> getBinanceData() {
        try {
            List<BinanceBean> allCurrencies = binanceFeignClient.getBinanceData();
            List<BinanceBean> currenciesEndWithUSDT = new ArrayList<>();
            for (BinanceBean binanceBean : allCurrencies) {
                if (binanceBean.getSymbol().endsWith("USDT")) {
                    String symbol = binanceBean.getSymbol().replace("USDT", "");
                    BigDecimal price = binanceBean.getPrice();

                    BinanceBean filteredBinanceBean = new BinanceBean(symbol, price);
                    currenciesEndWithUSDT.add(filteredBinanceBean);
                }
            }

            return currenciesEndWithUSDT;
        } catch (Exception e) {
            log.error("An error occurred while getting ticker prices", e);
            throw new RuntimeException("error occurred currencies");
        }
    }
}