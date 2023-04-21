package io.app.arbittrading.currency.service.impl;

import io.app.arbittrading.binance.bean.BinanceBean;
import io.app.arbittrading.binance.client.BinanceFeignClient;
import io.app.arbittrading.cex.bean.CexBean;
import io.app.arbittrading.currency.bean.CurrencyBean;
import io.app.arbittrading.currency.client.CurrencyFeignClient;
import io.app.arbittrading.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyFeignClient currencyFeignClient;
    @Override
    public List<CurrencyBean> getCurrencyData() {
        try {
            List<CurrencyBean> allCurrencies = currencyFeignClient.getCurrencyData();
            List<CurrencyBean> currenciesEndWithUSDT = new ArrayList<>();
            for (CurrencyBean currencyBean : allCurrencies) {
                if (currencyBean.getSymbol().endsWith("/USDT")) {
                    String symbol = currencyBean.getSymbol().replace("/USDT", "");
                    BigDecimal price = currencyBean.getLastPrice();

                    CurrencyBean filteredBinanceBean = new CurrencyBean(symbol, price);
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
