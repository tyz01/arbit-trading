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
    protected String apiKey;
    @Value("${binance.secretKey}")
    protected String secretKey;
    @Value("${binance.apiBaseUrl}")
    protected String apiBaseUrl;

}
