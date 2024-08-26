package lissa.trading.tinkoff.stock.service.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecurityPosition {
    private String figi;
    private long blocked;
    private long balance;
}
