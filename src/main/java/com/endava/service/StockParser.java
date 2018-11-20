package com.endava.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StockParser {

    private static final Map<String, DoubleHolder> doubleCache = new ConcurrentHashMap<>(100_000);

    static {
        warmCache();
        System.out.println("Double cache successfully warmed");
    }

    public static double[] getStocks(String stockPrices) {
        try {
            if (stockPrices == null || stockPrices.trim().isEmpty()) {
                return new double[0];
            }
            stockPrices = stockPrices.trim();
            String[] items = stockPrices.split(" ");
            double[] stocks = new double[items.length];
            int i = 0;
            for (String price : items) {
                if (price.contains("-")) {
                    return new double[0];
                }
                if (doubleCache.containsKey(price)) {
                    stocks[i] = doubleCache.get(price).value;
                } else {
                    stocks[i] = Double.parseDouble(price);
                    doubleCache.put(price, new DoubleHolder(stocks[i]));
                }
                ++i;
            }
            return stocks;
        } catch (NumberFormatException e) {
            return new double[0];
        }
    }

    private static void warmCache() {
        for (int i = 5; i <= 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                double value = i + j /100.0;
                value = ((int) (value * 100)) / 100.0;
                doubleCache.put(Double.toString(value), new DoubleHolder(value));
            }
        }
    }

    private static class DoubleHolder {

        private final double value;

        private DoubleHolder(final double value) {
            this.value = value;
        }
    }
}
