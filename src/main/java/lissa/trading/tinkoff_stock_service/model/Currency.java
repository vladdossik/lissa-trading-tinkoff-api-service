package lissa.trading.tinkoff_stock_service.model;

import java.util.HashMap;
import java.util.Map;

public enum Currency {
    RUB, USD, EUR, GBP, HKD, CHF, JPY, CNY, TRY;

    private static final Map<String, Currency> currencyMap = new HashMap<>();

    static {
        for (Currency currency : Currency.values()) {
            currencyMap.put(currency.name().toLowerCase(), currency);
        }
    }

    public static Currency fromString(String currency) {
        if (currency == null || !currencyMap.containsKey(currency.toLowerCase())) {
            throw new IllegalArgumentException("Unknown currency: " + currency);
        }
        return currencyMap.get(currency.toLowerCase());
    }
}