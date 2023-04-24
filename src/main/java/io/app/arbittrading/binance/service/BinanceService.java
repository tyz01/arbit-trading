package io.app.arbittrading.binance.service;

import io.app.arbittrading.binance.bean.BinanceBean;

import java.util.List;

public interface BinanceService {
    List<BinanceBean> getBinanceData();
}
