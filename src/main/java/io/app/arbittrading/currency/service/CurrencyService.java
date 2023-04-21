package io.app.arbittrading.currency.service;

import io.app.arbittrading.currency.bean.CurrencyBean;

import java.util.List;

public interface CurrencyService {
    List<CurrencyBean> getCurrencyData();
}
