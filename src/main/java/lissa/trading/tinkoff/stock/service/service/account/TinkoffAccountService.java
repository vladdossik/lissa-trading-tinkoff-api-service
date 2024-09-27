package lissa.trading.tinkoff.stock.service.service.account;

import lissa.trading.tinkoff.stock.service.dto.account.AccountInfoDto;
import lissa.trading.tinkoff.stock.service.dto.account.FavouriteStocksDto;
import lissa.trading.tinkoff.stock.service.dto.account.MarginAttributesDto;
import lissa.trading.tinkoff.stock.service.dto.account.BalanceDto;
import lissa.trading.tinkoff.stock.service.dto.account.SecurityPositionsDto;
import lissa.trading.tinkoff.stock.service.exception.AccountInfoException;
import lissa.trading.tinkoff.stock.service.exception.BalanceRetrievalException;
import lissa.trading.tinkoff.stock.service.exception.MarginAttributesRetrievalException;
import lissa.trading.tinkoff.stock.service.exception.SecuritiesNotFoundException;
import lissa.trading.tinkoff.stock.service.exception.StockRetrievalException;
import lissa.trading.tinkoff.stock.service.dto.account.SecurityPosition;
import lissa.trading.tinkoff.stock.service.model.UserAccount;
import lissa.trading.tinkoff.stock.service.service.AsyncTinkoffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.GetInfoResponse;
import ru.tinkoff.piapi.contract.v1.GetMarginAttributesResponse;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.OperationsService;
import ru.tinkoff.piapi.core.UsersService;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class TinkoffAccountService implements AccountService {
    private final UsersService usersService;
    private final OperationsService operationsService;
    private final AsyncTinkoffService asyncTinkoffService;


    public TinkoffAccountService(InvestApi investApi, AsyncTinkoffService asyncTinkoffService) {
        this.usersService = investApi.getUserService();
        this.operationsService = investApi.getOperationsService();
        this.asyncTinkoffService = asyncTinkoffService;
    }

    @Override
    public AccountInfoDto getAccountsInfo() {
        try {
            GetInfoResponse info = usersService.getInfoSync();
            List<UserAccount> accounts = usersService.getAccountsSync().stream()
                    .map(account -> new UserAccount(
                            account.getId(),
                            info.getTariff(),
                            info.getPremStatus()
                    ))
                    .toList();
            return new AccountInfoDto(accounts);
        } catch (Exception e) {
            log.error("Failed to retrieve accounts info.", e);
            throw new AccountInfoException("Failed to retrieve accounts info.", e);
        }
    }

    @Override
    public BalanceDto getBalance(String accountId) {
        try {
            Portfolio portfolio = operationsService.getPortfolioSync(accountId);
            return new BalanceDto(
                    portfolio.getTotalAmountPortfolio().getCurrency(),
                    portfolio.getTotalAmountCurrencies().getValue(),
                    portfolio.getTotalAmountPortfolio().getValue()
            );
        } catch (Exception e) {
            log.error("Failed to retrieve balance for account {}.", accountId, e);
            throw new BalanceRetrievalException("Failed to retrieve balance.", e);
        }
    }

    @Override
    public MarginAttributesDto getMarginAttributes(String accountId) {
        try {
            GetMarginAttributesResponse marginAttributes = usersService.getMarginAttributesSync(accountId);
            double liquidPortfolioValue = marginAttributes.getLiquidPortfolio().getUnits() +
                    marginAttributes.getLiquidPortfolio().getNano() / 1_000_000_000.0;
            return new MarginAttributesDto(
                    marginAttributes.getLiquidPortfolio().getCurrency(),
                    liquidPortfolioValue
            );
        } catch (Exception e) {
            log.error("Failed to retrieve margin attributes for account {}.", accountId, e);
            if (e.getMessage().contains("Account margin status is disabled")) {
                return null;
            }
            throw new MarginAttributesRetrievalException("Failed to retrieve margin attributes.", e);
        }
    }

    @Override
    public FavouriteStocksDto getFavouriteStocks() {
        try {
            List<CompletableFuture<String>> favoriteStockFutures = asyncTinkoffService.getFavoriteInstruments().get().stream()
                    .map(instrument -> CompletableFuture.completedFuture(instrument.getTicker()))
                    .toList();

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    favoriteStockFutures.toArray(new CompletableFuture[0])
            );

            List<String> tickers = allFutures.thenApply(v ->
                    favoriteStockFutures.stream()
                            .map(CompletableFuture::join)
                            .toList()
            ).join();

            return new FavouriteStocksDto(tickers);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while retrieving favourite stocks.", e);
            throw new StockRetrievalException("Failed to retrieve favourite stocks due to thread interruption.", e);
        } catch (ExecutionException e) {
            log.error("Failed to retrieve favourite stocks.", e);
            throw new StockRetrievalException("Failed to retrieve favourite stocks.", e);
        }
    }

    @Override
    public SecurityPositionsDto getPositionsById(String accountId) {
        try {
            List<CompletableFuture<SecurityPosition>> positionFutures = asyncTinkoffService.getPositionsById(accountId).get().stream()
                    .map(position -> CompletableFuture.completedFuture(new SecurityPosition(
                            position.getFigi(),
                            position.getBlocked(),
                            position.getBalance()
                    )))
                    .toList();

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    positionFutures.toArray(new CompletableFuture[0])
            );

            List<SecurityPosition> positions = allFutures.thenApply(v ->
                    positionFutures.stream()
                            .map(CompletableFuture::join)
                            .toList()
            ).join();

            return new SecurityPositionsDto(positions);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while retrieving positions for account {}.", accountId, e);
            throw new SecuritiesNotFoundException("Failed to retrieve positions due to thread interruption.", e);
        } catch (ExecutionException e) {
            log.error("Failed to retrieve positions for account {}.", accountId, e);
            throw new SecuritiesNotFoundException("Failed to retrieve positions.", e);
        }
    }
}