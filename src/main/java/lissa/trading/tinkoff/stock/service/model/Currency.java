package lissa.trading.tinkoff.stock.service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Currency {
    RUB, USD, EUR, GBP, HKD, CHF, JPY, CNY, TRY;

    public static Currency getFromString(String currency) {
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(currency))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown currency: " + currency));
    }
}
