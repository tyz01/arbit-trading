package io.app.arbittrading.cex.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CexBean {
    private String e;
    private String ok;
    private List<PriceData> data;

    @Data
    public static class PriceData {
        private String symbol1;
        private String symbol2;
        private BigDecimal lprice;

    }
}
