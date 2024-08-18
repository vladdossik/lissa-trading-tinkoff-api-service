package lissa.trading.tinkoff_stock_service.service.account;

import lissa.trading.tinkoff_stock_service.dto.account.AccountInfoDto;
import lissa.trading.tinkoff_stock_service.dto.account.BalanceDto;
import lissa.trading.tinkoff_stock_service.dto.account.FavoutiteStocksDto;
import lissa.trading.tinkoff_stock_service.dto.account.MarginAttributesDto;
import lissa.trading.tinkoff_stock_service.dto.account.SecurityPositionsDto;

public interface AccountService {
    AccountInfoDto getAccountsInfo();

    BalanceDto getBalance(String accountId);

    MarginAttributesDto getMarginAttributes(String accountId);

    FavoutiteStocksDto getFavouriteStocks();

    SecurityPositionsDto getPositionsById(String accountId);
}