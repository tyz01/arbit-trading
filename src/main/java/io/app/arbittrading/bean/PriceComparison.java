package io.app.arbittrading.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceComparison {
    private String currencyPair;
    private Double priceDiff;
    private String higherExchange;
    private Double priceFromBinance;
    private Double priceFromByBit;
}
