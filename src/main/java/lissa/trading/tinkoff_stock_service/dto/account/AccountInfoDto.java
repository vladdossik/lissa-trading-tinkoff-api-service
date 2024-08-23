package lissa.trading.tinkoff_stock_service.dto.account;

import lissa.trading.tinkoff_stock_service.model.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AccountInfoDto {
    private List<UserAccount> userAccounts;
}
