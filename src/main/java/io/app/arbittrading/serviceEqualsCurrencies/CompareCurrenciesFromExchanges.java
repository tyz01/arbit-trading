package io.app.arbittrading.serviceEqualsCurrencies;

import io.app.arbittrading.binance.bean.BinanceBean;
import io.app.arbittrading.binance.service.BinanceService;
import io.app.arbittrading.cex.bean.CexBean;
import io.app.arbittrading.cex.service.CexService;
import io.app.arbittrading.currency.bean.CurrencyBean;
import io.app.arbittrading.currency.service.CurrencyService;
import io.app.arbittrading.telegram.TelegramSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class CompareCurrenciesFromExchanges {
    private final CexService cexService;
    private final BinanceService binanceService;
    private final CurrencyService currencyExchangeService;
    private final TelegramSender telegramSender;
    public static int countSession = 1;
    private static final int PERCENTAGE_VALUE = 6;
    private List<Valuta> valutas;
    private static final Set<String> allNamesCurrencies = new HashSet<>();
    private static final List<String> exchangeName = new ArrayList<>();

    @Scheduled(fixedDelay = 30000)
    @Async
    public void SheduleForResponse() {
        List<CexBean.PriceData> cexCurrencyList = cexService.getCexData().getData();
        List<BinanceBean> binanceCurrencyList = binanceService.getBinanceData();
        List<CurrencyBean> currencyCurrencyList = currencyExchangeService.getCurrencyData();

        Map<String, Map<String, BigDecimal>> allExchanges = getAllExchanges(cexCurrencyList, binanceCurrencyList, currencyCurrencyList);

        for (Map.Entry<String, Map<String, BigDecimal>> entry : allExchanges.entrySet()) {
            exchangeName.add(entry.getKey());
        }
        
        fillListValutas(allExchanges);
        calculatePriceDifferencePercent();
        incrementAndLogSessionCount();
    }

    private Map<String, Map<String, BigDecimal>> getAllExchanges(List<CexBean.PriceData> cexCurrencyList, List<BinanceBean> binanceCurrencyList, List<CurrencyBean> currencyCurrencyList) {
        Map<String, BigDecimal> cexData = getDataFromCex(cexCurrencyList);
        Map<String, BigDecimal> binanceData = getDataFromBinance(binanceCurrencyList);
        Map<String, BigDecimal> currencyExchangeData = getDataFromCurrency(currencyCurrencyList);

        Map<String, Map<String, BigDecimal>> allExchanges = new HashMap<>();
        allExchanges.put("cex", cexData);
        allExchanges.put("binance", binanceData);
        allExchanges.put("currency", currencyExchangeData);
        return allExchanges;
    }

    private Map<String, BigDecimal> getDataFromCex(List<CexBean.PriceData> cexCurrencyList) {
        Map<String, BigDecimal> cexPrices = new HashMap<>();
        for (CexBean.PriceData cexData : cexCurrencyList) {
            cexPrices.put(cexData.getSymbol1(), cexData.getLprice());
            allNamesCurrencies.add(cexData.getSymbol1());
        }
        return cexPrices;
    }

    private Map<String, BigDecimal> getDataFromBinance(List<BinanceBean> binanceCurrencyList) {
        Map<String, BigDecimal> binancePrices = new HashMap<>();
        for (BinanceBean binancePriceData : binanceCurrencyList) {
            binancePrices.put(binancePriceData.getSymbol(), binancePriceData.getPrice());
            allNamesCurrencies.add(binancePriceData.getSymbol());
        }
        return binancePrices;
    }

    private Map<String, BigDecimal> getDataFromCurrency(List<CurrencyBean> currencyExchangeList) {
        Map<String, BigDecimal> currencyExchangePrices = new HashMap<>();
        for (CurrencyBean currencyExchangeData : currencyExchangeList) {
            currencyExchangePrices.put(currencyExchangeData.getSymbol(), currencyExchangeData.getLastPrice());
            allNamesCurrencies.add(currencyExchangeData.getSymbol());
        }
        return currencyExchangePrices;
    }

    private void fillListValutas(Map<String, Map<String, BigDecimal>> allExchanges) {
        for (String name : allNamesCurrencies) {
            Valuta valuta = new Valuta();
            valuta.exchangeNameAndPrice = new HashMap<>();
            for (String exchange : exchangeName) {
                valuta.exchangeNameAndPrice.put(exchange, allExchanges.get(exchange).get(name));
            }
            valuta.nameValuta = name;
            valutas.add(valuta);
        }

    }

    private void calculatePriceDifferencePercent() {
        for (Valuta valuta : valutas) {
            BigDecimal cexPrice = valuta.exchangeNameAndPrice.get("cex");
            BigDecimal binancePrice = valuta.exchangeNameAndPrice.get("binance");
            BigDecimal currencyPrice = valuta.exchangeNameAndPrice.get("currency");

            BigDecimal diffPercentCexBinance = null;
            BigDecimal diffPercentCexCurrency = null;
            BigDecimal diffPercentBinanceCurrency = null;

            if (cexPrice != null && binancePrice != null) {
                diffPercentCexBinance = calculatePercentageDifference(cexPrice, binancePrice);
            }

            if (cexPrice != null && currencyPrice != null) {
                diffPercentCexCurrency = calculatePercentageDifference(cexPrice, currencyPrice);
            }

            if (binancePrice != null && currencyPrice != null) {
                diffPercentBinanceCurrency = calculatePercentageDifference(binancePrice, currencyPrice);
            }

            sendMessageInTelegram(valuta, cexPrice, binancePrice, currencyPrice, diffPercentCexBinance, diffPercentCexCurrency, diffPercentBinanceCurrency);

        }
    }

    private void sendMessageInTelegram(Valuta valuta, BigDecimal cexPrice, BigDecimal binancePrice, BigDecimal currencyPrice, BigDecimal diffPercentCexBinance, BigDecimal diffPercentCexCurrency, BigDecimal diffPercentBinanceCurrency) {
        if (diffPercentCexBinance != null && diffPercentCexBinance.abs().compareTo(new BigDecimal(PERCENTAGE_VALUE)) > 0
                || diffPercentCexCurrency != null && diffPercentCexCurrency.abs().compareTo(new BigDecimal(PERCENTAGE_VALUE)) > 0
                || diffPercentBinanceCurrency != null && diffPercentBinanceCurrency.abs().compareTo(new BigDecimal(PERCENTAGE_VALUE)) > 0) {
            String message = formatPriceDifferenceMessage(valuta.nameValuta, diffPercentCexBinance, diffPercentCexCurrency,
                    diffPercentBinanceCurrency, cexPrice, binancePrice, currencyPrice);
            telegramSender.sendMessage(message);
        }
    }

    private BigDecimal calculatePercentageDifference(BigDecimal oldValue, BigDecimal newValue) {
        return oldValue.subtract(newValue).divide(oldValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    private String formatPriceDifferenceMessage(String valutaName, BigDecimal diffPercentCexBinance,
                                                BigDecimal diffPercentCexCurrency, BigDecimal diffPercentBinanceCurrency,
                                                BigDecimal cexPrice, BigDecimal binancePrice, BigDecimal currencyPrice) {
        String message = "Valuta: " + valutaName + "\n";
        message += "CEX vs Binance: " + (diffPercentCexBinance != null ? diffPercentCexBinance.abs() : "null") + "%\n";
        message += "CEX vs Currency: " + (diffPercentCexCurrency != null ? diffPercentCexCurrency.abs() : "null") + "%\n";
        message += "Binance vs Currency: " + (diffPercentBinanceCurrency != null ? diffPercentBinanceCurrency.abs() : "null") + "%\n";
        message += "CEX Price: " + (cexPrice != null ? cexPrice : "null") + "\n";
        message += "Binance Price: " + (binancePrice != null ? binancePrice : "null") + "\n";
        message += "Currency Price: " + (currencyPrice != null ? currencyPrice : "null") + "\n";
        return message;
    }

    private void incrementAndLogSessionCount() {
        telegramSender.sendMessage("session: " + countSession);
        log.info("session: " + countSession);
        countSession++;
    }
}
