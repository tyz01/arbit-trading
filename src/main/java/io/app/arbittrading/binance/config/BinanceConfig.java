package io.app.arbittrading.binance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class BinanceConfig {

    @Value("${binance.apiKey}")
    private String apiKey;
    @Value("${binance.secretKey}")
    private String secretKey;
    @Value("${binance.apiBaseUrl}")
    private String apiBaseUrl;

}
