package lissa.trading.tinkoff_stock_service.dto.account;

import lissa.trading.tinkoff_stock_service.model.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class AccountInfoDto {
    List<UserAccount> userAccounts;
}
