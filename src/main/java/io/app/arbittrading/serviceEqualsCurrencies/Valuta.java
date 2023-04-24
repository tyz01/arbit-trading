package io.app.arbittrading.serviceEqualsCurrencies;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class Valuta {
    String nameValuta;
    Map<String, BigDecimal> exchangeNameAndPrice = new HashMap<>();

}