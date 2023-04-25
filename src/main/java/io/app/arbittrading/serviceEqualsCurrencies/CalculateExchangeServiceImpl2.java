package io.app.arbittrading.serviceEqualsCurrencies;

import io.app.arbittrading.binance.bean.BinanceBean;
import io.app.arbittrading.binance.service.BinanceService;
import io.app.arbittrading.cex.bean.CexBean;
import io.app.arbittrading.cex.service.CexService;
import io.app.arbittrading.currency.bean.CurrencyBean;
import io.app.arbittrading.currency.service.CurrencyService;
import io.app.arbittrading.telegram.TelegramSender;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class CalculateExchangeServiceImpl2 implements CalculateExchangeService {
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

        final var allCurrency = allCurrencyNames.parallelStream()
                .map(currencyName -> prepareAllCurrency(currencyName, exchangesMap))
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()))
                );

        log.info("SEND EVENT TO THREAD POOL");
        service.execute(() -> {
            log.info("START FIND PAIR");
            allCurrency.values().parallelStream()
                    .flatMap(List::stream)
                    .forEach(this::calculate);
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
        final var minBean = currencies.stream()
                .min(Comparator.comparing(CurrencyInfoBean::getCurrencyPrice));
        final var maxBean = currencies.stream()
                .max(Comparator.comparing(CurrencyInfoBean::getCurrencyPrice));

        if (minBean.isPresent() && maxBean.isPresent()) {
            final var minPrice = minBean.get().getCurrencyPrice();
            final var maxPrice = maxBean.get().getCurrencyPrice();
            final var diff = maxPrice.subtract(minPrice).divide(minPrice, RoundingMode.HALF_UP);
            if (diff.compareTo(new BigDecimal(diffProc)) >= 0) {
                final var message = prepareMessage(maxBean.get(), minBean.get());
                final var percentageDiff = diff.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                final var link = generateLink(maxBean.get().getCurrencyName(), maxBean.get().getExchangeName());
                final var finalMessage = String.format("%s\nPercent difference: %s%%\n%s",
                        message, percentageDiff, link);
                service.execute(() -> telegramSender.sendMessage(finalMessage));
            }
        }
    }

    private String generateLink(String currencyName1, String exchangeName) {
        String link = "";
        switch (exchangeName) {
            case "cex" -> link = "https://cex.io/exchange/" + currencyName1 + "/" + "USDT";
            case "currency" ->
                    link = "https://api-adapter.backend.currency.com/exchange/" + currencyName1 + "-" + "USDT";
            case "binance" -> link = "https://api.binance.com/exchange/" + currencyName1 + "-" + "USDT";
            default -> log.info("exchange not exist");
        }
        return link;
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
