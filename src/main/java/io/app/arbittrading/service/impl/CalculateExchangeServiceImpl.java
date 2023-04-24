package io.app.arbittrading.service.impl;

import io.app.arbittrading.bean.CurrencyInfoBean;
import io.app.arbittrading.binance.bean.BinanceBean;
import io.app.arbittrading.binance.service.BinanceService;
import io.app.arbittrading.cex.bean.CexBean;
import io.app.arbittrading.cex.service.CexService;
import io.app.arbittrading.currency.bean.CurrencyBean;
import io.app.arbittrading.currency.service.CurrencyService;
import io.app.arbittrading.service.CalculateExchangeService;
import io.app.arbittrading.telegram.TelegramSender;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CalculateExchangeServiceImpl implements CalculateExchangeService {
    private final ExecutorService service = Executors.newFixedThreadPool(10);
    private final CexService cexService;
    private final BinanceService binanceService;
    private final CurrencyService currencyExchangeService;
    private final TelegramSender telegramSender;
    private static final String diffProc = "0.05";

    private Map<String, Map<String, BigDecimal>> initExchange() {
        final var dataFromCex = CexBean.getDataFromCex(cexService.getCexData().getData());
        final var dataFromBinance = BinanceBean.getDataFromBinance(binanceService.getBinanceData());
        final var dataFromCurrency = CurrencyBean.getDataFromCurrency(currencyExchangeService.getCurrencyData());

        Map<String, Map<String, BigDecimal>> allExchanges = new HashMap<>();

        allExchanges.put("Cex", dataFromCex);
        allExchanges.put("Binance", dataFromBinance);
        allExchanges.put("Currency", dataFromCurrency);
        return allExchanges;
    }

    @Override
    @Scheduled(fixedDelay = 30000)
    public void compareExchangePrices() {
        final var exchangesMap = initExchange();
        final var allCurrencyNames = exchangesMap.values().stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toSet());

        final Map<String, List<CurrencyInfoBean>> allCurrency = new HashMap<>();
        for (String currencyName : allCurrencyNames) {
            allCurrency.putAll(prepareAllCurrency(currencyName, exchangesMap));
        }

        service.execute(() ->
                allCurrencyNames.parallelStream()
                        .forEach(x -> calculate(allCurrency.get(x)))
        );
    }

    private Map<String, List<CurrencyInfoBean>> prepareAllCurrency(String currencyName,
                                                                   Map<String, Map<String, BigDecimal>> exchanges) {
        final List<CurrencyInfoBean> testBeans = new ArrayList<>();
        final Map<String, List<CurrencyInfoBean>> result = new HashMap<>();
        for (Map.Entry<String, Map<String, BigDecimal>> entry : exchanges.entrySet()) {
            testBeans.add(new CurrencyInfoBean(entry.getKey(), currencyName, entry.getValue().get(currencyName)));
        }
        result.put(currencyName, testBeans);
        return result;
    }

    @Async
    protected void calculate(List<CurrencyInfoBean> currencies) {
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        CurrencyInfoBean minBean = null;
        CurrencyInfoBean maxBean = null;

        for (CurrencyInfoBean bean : currencies) {
            BigDecimal price = bean.getCurrencyPrice();
            if (Objects.isNull(minPrice) || price.compareTo(minPrice) < 0) {
                minPrice = price;
                minBean = bean;
            }
            if (Objects.isNull(maxPrice) || price.compareTo(maxPrice) > 0) {
                maxPrice = price;
                maxBean = bean;
            }
        }

        if (Objects.nonNull(minPrice) && Objects.nonNull(maxPrice)) {
            BigDecimal diff = maxPrice.subtract(minPrice)
                    .divide(minPrice, BigDecimal.ROUND_HALF_UP);
            if (diff.compareTo(new BigDecimal(diffProc)) >= 0) {
                telegramSender.sendMessage(prepareMessage(maxBean, minBean));
            }
        }
    }

    private String prepareMessage(CurrencyInfoBean max, CurrencyInfoBean min) {
        return String.format("CURRENCY: %s\n%s price: %s\n%s price: %s",
                max.getCurrencyName(),
                max.getExchangeName(),
                max.getCurrencyPrice().toString(),
                min.getExchangeName(),
                min.getCurrencyPrice().toString());
    }
}
