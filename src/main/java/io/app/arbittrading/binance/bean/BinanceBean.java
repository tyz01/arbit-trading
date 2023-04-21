package io.app.arbittrading.binance.bean;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BinanceBean {
    private String symbol;
    private BigDecimal price;
}

