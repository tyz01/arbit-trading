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
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.app.arbittrading.serviceEqualsCurrencies.CompareCurrenciesFromExchanges.countSession;

@Service
@Log4j2
@AllArgsConstructor
public class CalculateExchangeServiceImpl implements CalculateExchangeService {
    private final ExecutorService service = Executors.newFixedThreadPool(10);
    private final CexService cexService;
    private final BinanceService binanceService;
    private final CurrencyService currencyExchangeService;
    private final TelegramSender telegramSender;
    private static final String diffProc = "0.05";

    private Map<String, Map<String, BigDecimal>> initExchange() {
        log.info("START INIT DATA");
        final var dataFromCex = CexBean.getDataFromCex(cexService.getCexData().getData());
        final var dataFromBinance = BinanceBean.getDataFromBinance(binanceService.getBinanceData());
        final var dataFromCurrency = CurrencyBean.getDataFromCurrency(currencyExchangeService.getCurrencyData());

        Map<String, Map<String, BigDecimal>> allExchanges = new HashMap<>();

        allExchanges.put("Cex", dataFromCex);
        allExchanges.put("Binance", dataFromBinance);
        allExchanges.put("Currency", dataFromCurrency);
        log.info("FINISH INIT DATA");
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

        log.info("SEND EVENT TO THREAD POOL");
        service.execute(() -> {
            log.info("START FIND PAIR");
            allCurrencyNames.parallelStream().forEach(x -> calculate(allCurrency.get(x)));
            incrementAndLogSessionCount();
            log.info("FINISH FIND PAIR");
        });
        log.info("SEND SUCCESS");
    }

    private Map<String, List<CurrencyInfoBean>> prepareAllCurrency(String currencyName,
                                                                   Map<String, Map<String, BigDecimal>> exchanges) {
        return exchanges.entrySet().parallelStream()
                .filter(Objects::nonNull)
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .filter(currencyEntry -> currencyEntry.getKey().equals(currencyName))
                        .map(currencyEntry -> new CurrencyInfoBean(entry.getKey(), currencyName, currencyEntry.getValue())))
                .collect(Collectors.groupingBy(CurrencyInfoBean::getCurrencyName));
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
            BigDecimal diff = maxPrice.subtract(minPrice).divide(minPrice, RoundingMode.HALF_UP);
            if (diff.compareTo(new BigDecimal(diffProc)) >= 0) {
                final var message = prepareMessage(maxBean, minBean);
                service.execute(() -> telegramSender.sendMessage(message));
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

    private void incrementAndLogSessionCount() {
        telegramSender.sendMessage("session: " + countSession);
        log.info("session: " + countSession);
        countSession++;
    }
}
