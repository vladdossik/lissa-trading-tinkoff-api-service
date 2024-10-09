package lissa.trading.tinkoff.stock.service.dto.account;

import lissa.trading.tinkoff.stock.service.model.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoDto {
    private List<UserAccount> userAccounts;
}
