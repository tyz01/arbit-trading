package io.app.arbittrading.currency.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrencyBean {
    private String symbol;
    private String priceChange;
    private String priceChangePercent;
    private String weightedAvgPrice;
    private BigDecimal lastPrice;
    private String lastQty;
    private String bidPrice;
    private String askPrice;
    private String highPrice;
    private String lowPrice;
    private String volume;
    private String quoteVolume;
    private long openTime;
    private long closeTime;

    public CurrencyBean(String symbol, BigDecimal lastPrice){
        this.symbol = symbol;
        this.lastPrice = lastPrice;
    }

    public static Map<String, BigDecimal> getDataFromCurrency(List<CurrencyBean> currencyExchangeList) {
        Map<String, BigDecimal> currencyExchangePrices = new HashMap<>();
        for (CurrencyBean currencyExchangeData : currencyExchangeList) {
            currencyExchangePrices.put(currencyExchangeData.getSymbol(), currencyExchangeData.getLastPrice());
        }
        return currencyExchangePrices;
    }

}
