package lissa.trading.tinkoff.stock.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAccount {
    private String accountId;
    private String tariff;
    private boolean premStatus;
}
