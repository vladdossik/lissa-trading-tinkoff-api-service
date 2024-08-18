package lissa.trading.tinkoff_stock_service.dto.account;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SecurityPosition {
    String figi;
    long blocked;
    long balance;
}
