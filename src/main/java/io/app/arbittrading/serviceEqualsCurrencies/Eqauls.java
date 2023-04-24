//package io.app.arbittrading.serviceEqualsCurrencies;
//
//import io.app.arbittrading.binance.bean.BinanceBean;
//import io.app.arbittrading.binance.service.BinanceService;
//import io.app.arbittrading.cex.bean.CexBean;
//import io.app.arbittrading.cex.service.CexService;
//import io.app.arbittrading.currency.bean.CurrencyBean;
//import io.app.arbittrading.currency.service.CurrencyService;
//import io.app.arbittrading.telegram.TelegramSender;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Component
//@AllArgsConstructor
//public class Eqauls {
//    private final CexService cexService;
//    private final BinanceService binanceService;
//    private final CurrencyService currencyExchangeService;
//    private final TelegramSender telegramSender;
//    private static int countSession = 1;
//    private static final int PERCENTAGE_VALUE = 5;
//
//    @Scheduled(fixedDelay = 30000)
//    @Async
//    public void calculateDifference() {
//        List<CexBean.PriceData> cexCurrencyList = cexService.getCexData().getData();
//        List<BinanceBean> binanceCurrencyList = binanceService.getBinanceData();
//        List<CurrencyBean> currencyCurrencyList = currencyExchangeService.getCurrencyData();
//
//        Map<String, BigDecimal> cexData = getDataFromCex(cexCurrencyList);
//        Map<String, BigDecimal> binanceData = getDataFromBinance(binanceCurrencyList);
//        Map<String, BigDecimal> currencyExchangeData = getDataFromCurrency(currencyCurrencyList);
//
//        comparePricesAndSendMessage(cexData, binanceData, currencyExchangeData);
//
//        incrementAndLogSessionCount();
//    }
//
//    private Map<String, BigDecimal> getDataFromCex(List<CexBean.PriceData> cexCurrencyList) {
//        Map<String, BigDecimal> cexPrices = new HashMap<>();
//        for (CexBean.PriceData cexData : cexCurrencyList) {
//            cexPrices.put(cexData.getSymbol1(), cexData.getLprice());
//        }
//        return cexPrices;
//    }
//
//    private Map<String, BigDecimal> getDataFromBinance(List<BinanceBean> binanceCurrencyList) {
//        Map<String, BigDecimal> binancePrices = new HashMap<>();
//        for (BinanceBean binancePriceData : binanceCurrencyList) {
//            binancePrices.put(binancePriceData.getSymbol(), binancePriceData.getPrice());
//        }
//        return binancePrices;
//    }
//
//    private Map<String, BigDecimal> getDataFromCurrency(List<CurrencyBean> currencyExchangeList) {
//        Map<String, BigDecimal> currencyExchangePrices = new HashMap<>();
//        for (CurrencyBean currencyExchangeData : currencyExchangeList) {
//            currencyExchangePrices.put(currencyExchangeData.getSymbol(), currencyExchangeData.getLastPrice());
//        }
//        return currencyExchangePrices;
//    }
//
//    private void comparePricesAndSendMessage(Map<String, BigDecimal> cexData, Map<String, BigDecimal> binanceData, Map<String, BigDecimal> currencyExchangeData) {
//        for (String currency : cexData.keySet()) {
//            if (binanceData.containsKey(currency) && currencyExchangeData.containsKey(currency)) {
//                final BigDecimal cexPrice = cexData.get(currency);
//                final BigDecimal binancePrice = binanceData.get(currency);
//                final BigDecimal currencyExchangePrice = currencyExchangeData.get(currency);
//
//                final BigDecimal cexDiffPercent = calculatePriceDifferencePercent(cexPrice, binancePrice);
//                final BigDecimal currencyExchangeDiffPercent = calculatePriceDifferencePercent(currencyExchangePrice, binancePrice);
//
//                if (cexDiffPercent.abs().compareTo(BigDecimal.valueOf(PERCENTAGE_VALUE)) > 0 || currencyExchangeDiffPercent.abs().compareTo(BigDecimal.valueOf(PERCENTAGE_VALUE)) > 0) {
//                    String message = formatPriceDifferenceMessage(currency, cexDiffPercent, currencyExchangeDiffPercent, cexPrice, binancePrice, currencyExchangePrice);
//                    telegramSender.sendMessage(message);
//                }
//            }
//        }
//    }
//
//    private BigDecimal calculatePriceDifferencePercent(BigDecimal price1, BigDecimal price2) {
//        return price1.subtract(price2)
//                .multiply(BigDecimal.valueOf(100))
//                .divide(price2, 2, RoundingMode.HALF_UP);
//    }
//
//    private String formatPriceDifferenceMessage(String currency, BigDecimal diffPercentPrice1, BigDecimal diffPercentPrice2, BigDecimal cexPrice, BigDecimal binancePrice, BigDecimal currencyPrice) {
//        String urlOnBuyCex = "https://cex.io/";
//        String urlOnBuyBinance = "https://www.binance.com/";
//        String urlOnBuyCurrency = "https://www.currency.com/";
//        String urlOnSellCex = "https://cex.io/";
//        String urlOnSellBinance = "https://www.binance.com/";
//        String urlOnSellCurrency = "https://www.currency.com/";
//        String message;
//
//        if (cexPrice.compareTo(binancePrice) > 0 && cexPrice.compareTo(currencyPrice) > 0) {
//            message = String.format("""
//                            currency %s: Cex has a higher price than Binance %s and Currency by %.2f%%\s
//                            cex price: %s\s
//                            binance price: %s\s
//                            currency price: %s\s
//                            url on Cex: %s
//                            url on Binance: %s
//                            url on Currency: %s""",
//                    currency, diffPercentPrice1.abs(), diffPercentPrice2.abs(), cexPrice, binancePrice, currencyPrice, urlOnSellCex, urlOnBuyBinance, urlOnBuyCurrency);
//        } else if (binancePrice.compareTo(cexPrice) > 0 && binancePrice.compareTo(currencyPrice) > 0) {
//            message = String.format("""
//                            currency %s: Binance has a higher price than Cex %s and Currency by %.2f%%\s
//                            binance price: %s\s
//                            cex price: %s\s
//                            currency.com price: %s\s
//                            url on Binance: %s
//                            url on Cex: %s
//                            url on Currency: %s""",
//                    currency, diffPercentPrice1.abs(), diffPercentPrice2.abs(), binancePrice, cexPrice, currencyPrice, urlOnSellBinance, urlOnBuyCex, urlOnBuyCurrency);
//        } else if (currencyPrice.compareTo(cexPrice) > 0 && currencyPrice.compareTo(binancePrice) > 0) {
//            message = String.format("""
//                            currency %s: Currency has a higher price than Cex %s and Binance by %.2f%%\s
//                            currency price: %s\s
//                            cex price: %s\s
//                            binance price: %s\s
//                            url on Currency.com: %s
//                            url on Cex: %s
//                            url on Binance: %s""",
//                    currency, diffPercentPrice1.abs(), diffPercentPrice2.abs(), currencyPrice, cexPrice, binancePrice, urlOnSellCurrency, urlOnBuyCex, urlOnBuyBinance);
//        } else {
//            message = "Could not determine exchange with highest price for currency " + currency;
//        }
//
//        return message;
//    }
//
//    private void incrementAndLogSessionCount() {
//        telegramSender.sendMessage("session: " + countSession);
//        log.info("session: " + countSession);
//        countSession++;
//    }
//}
