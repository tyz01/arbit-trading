package io.app.arbittrading.currency.client;

import io.app.arbittrading.currency.bean.CurrencyBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "currencyFeignClient", url = "https://api-adapter.backend.currency.com/")
public interface CurrencyFeignClient {
    @GetMapping("/api/v2/ticker/24hr")
    List<CurrencyBean> getCurrencyData();
}
