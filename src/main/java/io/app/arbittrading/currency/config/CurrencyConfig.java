package io.app.arbittrading.currency.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConfig {
    @Value("${currency.apiKey}")
    protected String apiKey;
    @Value("${currency.apiSecret}")
    protected String secretKey;
    @Value("${currency.apiUrl}")
    protected String apiBaseUrl;

}
