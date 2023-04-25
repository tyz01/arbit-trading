package io.app.arbittrading.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyInfoBean {
    private String exchangeName;
    private String currencyName;
    private BigDecimal currencyPrice;
}
