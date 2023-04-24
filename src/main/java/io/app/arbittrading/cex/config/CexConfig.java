package io.app.arbittrading.cex.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CexConfig {
    @Value("${cex.apiKey}")
    protected String apiKey;
    @Value("${cex.apiSecret}")
    protected String secretKey;
    @Value("${cex.apiUrl}")
    protected String apiBaseUrl;

        @Bean
        public Decoder feignDecoder() {
            return new JacksonDecoder(new ObjectMapper());
        }
}
