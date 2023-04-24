package io.app.arbittrading.currency.controller;

import io.app.arbittrading.currency.bean.CurrencyBean;
import io.app.arbittrading.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/ticker-prices")
    public List<CurrencyBean> getTickerPrices() {
        return currencyService.getCurrencyData();
    }
}
