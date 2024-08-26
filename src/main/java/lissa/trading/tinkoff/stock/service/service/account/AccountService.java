package lissa.trading.tinkoff.stock.service.service.account;

import lissa.trading.tinkoff.stock.service.dto.account.AccountInfoDto;
import lissa.trading.tinkoff.stock.service.dto.account.BalanceDto;
import lissa.trading.tinkoff.stock.service.dto.account.FavouriteStocksDto;
import lissa.trading.tinkoff.stock.service.dto.account.MarginAttributesDto;
import lissa.trading.tinkoff.stock.service.dto.account.SecurityPositionsDto;

public interface AccountService {
    AccountInfoDto getAccountsInfo();

    BalanceDto getBalance(String accountId);

    MarginAttributesDto getMarginAttributes(String accountId);

    FavouriteStocksDto getFavouriteStocks();

    SecurityPositionsDto getPositionsById(String accountId);
}