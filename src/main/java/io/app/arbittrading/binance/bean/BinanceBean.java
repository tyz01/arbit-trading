package io.app.arbittrading.binance.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinanceBean {
    private String symbol;
    private BigDecimal price;

    public static Map<String, BigDecimal> getDataFromBinance(List<BinanceBean> binanceCurrencyList) {
        Map<String, BigDecimal> binancePrices = new HashMap<>();
        for (BinanceBean binancePriceData : binanceCurrencyList) {
            binancePrices.put(binancePriceData.getSymbol(), binancePriceData.getPrice());
        }
        return binancePrices;
    }
}

