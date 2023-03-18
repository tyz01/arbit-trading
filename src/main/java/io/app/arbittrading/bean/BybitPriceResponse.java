package io.app.arbittrading.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BybitPriceResponse {
    private String symbol;
    private Double last_price;
}
