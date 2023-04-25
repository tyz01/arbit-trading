package io.app.arbittrading.cex.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CexBean {
    private String e;
    private String ok;
    private List<PriceData> data;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class PriceData {
        private String symbol1;
        private String symbol2;
        private BigDecimal lprice;
    }

    public static Map<String, BigDecimal> getDataFromCex(List<PriceData> cexCurrencyList) {
        Map<String, BigDecimal> cexPrices = new HashMap<>();
        for (CexBean.PriceData cexData : cexCurrencyList) {
            cexPrices.put(cexData.getSymbol1(), cexData.getLprice());
        }
        return cexPrices;
    }
}
