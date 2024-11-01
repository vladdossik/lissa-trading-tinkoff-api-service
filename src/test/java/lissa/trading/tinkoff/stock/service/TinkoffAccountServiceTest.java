package lissa.trading.tinkoff.stock.service;

import lissa.trading.tinkoff.stock.service.dto.account.AccountInfoDto;
import lissa.trading.tinkoff.stock.service.dto.account.BalanceDto;
import lissa.trading.tinkoff.stock.service.dto.account.FavouriteStocksDto;
import lissa.trading.tinkoff.stock.service.dto.account.MarginAttributesDto;
import lissa.trading.tinkoff.stock.service.dto.account.SecurityPositionDto;
import lissa.trading.tinkoff.stock.service.dto.account.SecurityPositionsDto;
import lissa.trading.tinkoff.stock.service.exception.AccountInfoException;
import lissa.trading.tinkoff.stock.service.exception.BalanceRetrievalException;
import lissa.trading.tinkoff.stock.service.exception.MarginAttributesRetrievalException;
import lissa.trading.tinkoff.stock.service.exception.SecuritiesNotFoundException;
import lissa.trading.tinkoff.stock.service.exception.StockRetrievalException;
import lissa.trading.tinkoff.stock.service.model.UserAccount;
import org.junit.jupiter.api.Test;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.FavoriteInstrument;
import ru.tinkoff.piapi.contract.v1.GetInfoResponse;
import ru.tinkoff.piapi.contract.v1.GetMarginAttributesResponse;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.core.models.Money;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.SecurityPosition;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TinkoffAccountServiceTest extends BaseTest {

    @Test
    void testGetAccountsInfo_Success() {
        GetInfoResponse infoResponseMock = mock(GetInfoResponse.class);
        when(infoResponseMock.getTariff()).thenReturn("Standard");
        when(infoResponseMock.getPremStatus()).thenReturn(false);

        when(usersServiceMock.getInfoSync()).thenReturn(infoResponseMock);

        Account accountMock = mock(Account.class);
        when(accountMock.getId()).thenReturn("accountId1");

        when(usersServiceMock.getAccountsSync()).thenReturn(List.of(accountMock));

        AccountInfoDto result = tinkoffAccountService.getAccountsInfo();

        assertNotNull(result);
        assertEquals(1, result.getUserAccounts().size());

        UserAccount userAccount = result.getUserAccounts().get(0);
        assertEquals("accountId1", userAccount.getAccountId());
        assertEquals("Standard", userAccount.getTariff());
        assertFalse(userAccount.isPremStatus());
    }

    @Test
    void testGetAccountsInfo_Exception() {
        when(usersServiceMock.getInfoSync()).thenThrow(new RuntimeException("Some error"));

        AccountInfoException exception = assertThrows(AccountInfoException.class, () -> {
            tinkoffAccountService.getAccountsInfo();
        });

        assertEquals("Failed to retrieve accounts info.", exception.getMessage());
    }

    @Test
    void testGetBalance_Success() {
        Portfolio portfolioMock = mock(Portfolio.class);
        Money totalAmountPortfolio = Money.builder().currency("USD").value(BigDecimal.valueOf(100)).build();
        Money totalAmountCurrencies = Money.builder().currency("USD").value(BigDecimal.valueOf(50)).build();

        when(portfolioMock.getTotalAmountPortfolio()).thenReturn(totalAmountPortfolio);
        when(portfolioMock.getTotalAmountCurrencies()).thenReturn(totalAmountCurrencies);

        when(operationsServiceMock.getPortfolioSync("accountId1")).thenReturn(portfolioMock);

        BalanceDto result = tinkoffAccountService.getBalance("accountId1");

        assertNotNull(result);
        assertEquals("USD", result.getCurrency());
        assertEquals(new BigDecimal("50"), result.getCurrentBalance());
        assertEquals(new BigDecimal("100"), result.getTotalAmountBalance());
    }

    @Test
    void testGetBalance_Exception() {
        when(operationsServiceMock.getPortfolioSync("accountId1")).thenThrow(new RuntimeException("Some error"));

        BalanceRetrievalException exception = assertThrows(BalanceRetrievalException.class, () -> {
            tinkoffAccountService.getBalance("accountId1");
        });

        assertEquals("Failed to retrieve balance.", exception.getMessage());
    }

    @Test
    void testGetMarginAttributes_Success() {
        GetMarginAttributesResponse marginAttributesMock = mock(GetMarginAttributesResponse.class);
        MoneyValue liquidPortfolio = MoneyValue.newBuilder().setCurrency("USD").setUnits(100).setNano(500_000_000).build();

        when(marginAttributesMock.getLiquidPortfolio()).thenReturn(liquidPortfolio);
        when(usersServiceMock.getMarginAttributesSync("accountId1")).thenReturn(marginAttributesMock);

        MarginAttributesDto result = tinkoffAccountService.getMarginAttributes("accountId1");

        assertNotNull(result);
        assertEquals("USD", result.getCurrency());
        assertEquals(100.5, result.getLiquidPortfolio());
    }

    @Test
    void testGetMarginAttributes_Disabled() {
        RuntimeException exception = new RuntimeException("Account margin status is disabled");
        when(usersServiceMock.getMarginAttributesSync("accountId1")).thenThrow(exception);

        MarginAttributesDto result = tinkoffAccountService.getMarginAttributes("accountId1");

        assertNull(result);
    }

    @Test
    void testGetMarginAttributes_Exception() {
        RuntimeException exception = new RuntimeException("Some other error");
        when(usersServiceMock.getMarginAttributesSync("accountId1")).thenThrow(exception);

        MarginAttributesRetrievalException thrownException = assertThrows(MarginAttributesRetrievalException.class, () -> {
            tinkoffAccountService.getMarginAttributes("accountId1");
        });

        assertEquals("Failed to retrieve margin attributes.", thrownException.getMessage());
    }


    @Test
    void testGetFavouriteStocks_Success() {
        FavoriteInstrument instrumentMock1 = mock(FavoriteInstrument.class);
        when(instrumentMock1.getTicker()).thenReturn("AAPL");
        FavoriteInstrument instrumentMock2 = mock(FavoriteInstrument.class);
        when(instrumentMock2.getTicker()).thenReturn("GOOGL");

        CompletableFuture<List<FavoriteInstrument>> futureInstruments = CompletableFuture.completedFuture(List.of(instrumentMock1, instrumentMock2));
        when(asyncTinkoffServiceMock.getFavoriteInstruments()).thenReturn(futureInstruments);

        FavouriteStocksDto result = tinkoffAccountService.getFavouriteStocks();

        assertNotNull(result);
        assertEquals(2, result.getFavouriteStocks().size());
        assertTrue(result.getFavouriteStocks().contains("AAPL"));
        assertTrue(result.getFavouriteStocks().contains("GOOGL"));
    }

    @Test
    void testGetFavouriteStocks_InterruptedException() {
        CompletableFuture<List<FavoriteInstrument>> futureInstruments = new CompletableFuture<>();
        futureInstruments.completeExceptionally(new InterruptedException("Interrupted"));
        when(asyncTinkoffServiceMock.getFavoriteInstruments()).thenReturn(futureInstruments);

        StockRetrievalException exception = assertThrows(StockRetrievalException.class, () -> {
            tinkoffAccountService.getFavouriteStocks();
        });

        assertEquals("Failed to retrieve favourite stocks.", exception.getMessage());
    }

    @Test
    void testGetFavouriteStocks_ExecutionException() {
        CompletableFuture<List<FavoriteInstrument>> futureInstruments = new CompletableFuture<>();
        futureInstruments.completeExceptionally(new ExecutionException(new Exception("Some error")));
        when(asyncTinkoffServiceMock.getFavoriteInstruments()).thenReturn(futureInstruments);

        StockRetrievalException exception = assertThrows(StockRetrievalException.class, () -> {
            tinkoffAccountService.getFavouriteStocks();
        });

        assertEquals("Failed to retrieve favourite stocks.", exception.getMessage());
    }

    @Test
    void testGetPositionsById_Success() {
        SecurityPosition position1 = mock(SecurityPosition.class);
        when(position1.getFigi()).thenReturn("FIGI1");
        when(position1.getBalance()).thenReturn(10L);
        when(position1.getBlocked()).thenReturn(0L);

        SecurityPosition position2 = mock(SecurityPosition.class);
        when(position2.getFigi()).thenReturn("FIGI2");
        when(position2.getBalance()).thenReturn(20L);
        when(position2.getBlocked()).thenReturn(5L);

        CompletableFuture<List<SecurityPosition>> futurePositions = CompletableFuture.completedFuture(List.of(position1, position2));
        when(asyncTinkoffServiceMock.getPositionsById("accountId1")).thenReturn(futurePositions);

        SecurityPositionsDto result = tinkoffAccountService.getPositionsById("accountId1");

        assertNotNull(result);
        assertEquals(2, result.getPositions().size());

        SecurityPositionDto dto1 = result.getPositions().stream().filter(p -> p.getFigi().equals("FIGI1")).findFirst().orElse(null);
        assertNotNull(dto1);
        assertEquals("FIGI1", dto1.getFigi());
        assertEquals(10L, dto1.getBalance());
        assertEquals(0L, dto1.getBlocked());

        SecurityPositionDto dto2 = result.getPositions().stream().filter(p -> p.getFigi().equals("FIGI2")).findFirst().orElse(null);
        assertNotNull(dto2);
        assertEquals("FIGI2", dto2.getFigi());
        assertEquals(20L, dto2.getBalance());
        assertEquals(5L, dto2.getBlocked());
    }

    @Test
    void testGetPositionsById_InterruptedException() {
        CompletableFuture<List<SecurityPosition>> futurePositions = new CompletableFuture<>();
        futurePositions.completeExceptionally(new InterruptedException("Interrupted"));
        when(asyncTinkoffServiceMock.getPositionsById("accountId1")).thenReturn(futurePositions);

        SecuritiesNotFoundException exception = assertThrows(SecuritiesNotFoundException.class, () -> {
            tinkoffAccountService.getPositionsById("accountId1");
        });

        assertEquals("Failed to retrieve positions.", exception.getMessage());
    }

    @Test
    void testGetPositionsById_ExecutionException() {
        CompletableFuture<List<SecurityPosition>> futurePositions = new CompletableFuture<>();
        futurePositions.completeExceptionally(new ExecutionException(new Exception("Some error")));
        when(asyncTinkoffServiceMock.getPositionsById("accountId1")).thenReturn(futurePositions);

        SecuritiesNotFoundException exception = assertThrows(SecuritiesNotFoundException.class, () -> {
            tinkoffAccountService.getPositionsById("accountId1");
        });

        assertEquals("Failed to retrieve positions.", exception.getMessage());
    }
}