package lissa.trading.tinkoff.stock.service;

import lissa.trading.tinkoff.stock.service.exception.SecuritiesNotFoundException;
import lissa.trading.tinkoff.stock.service.service.AsyncTinkoffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tinkoff.piapi.contract.v1.FavoriteInstrument;
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.InstrumentShort;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.OperationsService;
import ru.tinkoff.piapi.core.models.Positions;
import ru.tinkoff.piapi.core.models.SecurityPosition;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AsyncTinkoffServiceTest extends BaseTest {
    private AsyncTinkoffService asyncTinkoffService;

    private InstrumentsService instrumentsServiceMock;
    private MarketDataService marketDataServiceMock;
    private OperationsService operationsServiceMock;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        instrumentsServiceMock = mock(InstrumentsService.class);
        marketDataServiceMock = mock(MarketDataService.class);
        operationsServiceMock = mock(OperationsService.class);

        when(investApiMock.getInstrumentsService()).thenReturn(instrumentsServiceMock);
        when(investApiMock.getMarketDataService()).thenReturn(marketDataServiceMock);
        when(investApiMock.getOperationsService()).thenReturn(operationsServiceMock);

        asyncTinkoffService = new AsyncTinkoffService(investApiMock);
    }

    @Test
    void testGetInstrumentByTicker_Success() throws Exception {
        String ticker = "AAPL";
        String figi = "FIGI123";

        InstrumentShort instrumentShortMock = mock(InstrumentShort.class);
        when(instrumentShortMock.getFigi()).thenReturn(figi);
        when(instrumentShortMock.getTicker()).thenReturn(ticker);

        when(instrumentsServiceMock.findInstrument(ticker)).thenReturn(CompletableFuture.completedFuture(List.of(instrumentShortMock)));

        Instrument instrumentMock = mock(Instrument.class);
        when(instrumentMock.getFigi()).thenReturn(figi);
        when(instrumentMock.getTicker()).thenReturn(ticker);

        when(instrumentsServiceMock.getInstrumentByFigi(figi)).thenReturn(CompletableFuture.completedFuture(instrumentMock));

        CompletableFuture<Optional<Instrument>> futureResult = asyncTinkoffService.getInstrumentByTicker(ticker);
        Optional<Instrument> result = futureResult.get(5, TimeUnit.SECONDS);

        assertTrue(result.isPresent());
        assertEquals(figi, result.get().getFigi());
        assertEquals(ticker, result.get().getTicker());
    }

    @Test
    void testGetInstrumentByTicker_NotFound() throws Exception {
        String ticker = "UNKNOWN";

        when(instrumentsServiceMock.findInstrument(ticker)).thenReturn(CompletableFuture.completedFuture(List.of()));

        CompletableFuture<Optional<Instrument>> futureResult = asyncTinkoffService.getInstrumentByTicker(ticker);
        Optional<Instrument> result = futureResult.get(5, TimeUnit.SECONDS);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetOrderBookByFigi_Success() throws Exception {
        String figi = "FIGI123";

        GetOrderBookResponse orderBookMock = mock(GetOrderBookResponse.class);
        when(orderBookMock.getFigi()).thenReturn(figi);

        when(marketDataServiceMock.getOrderBook(figi, 1)).thenReturn(CompletableFuture.completedFuture(orderBookMock));

        CompletableFuture<Optional<GetOrderBookResponse>> futureResult = asyncTinkoffService.getOrderBookByFigi(figi);
        Optional<GetOrderBookResponse> result = futureResult.get();

        assertTrue(result.isPresent());
        assertEquals(figi, result.get().getFigi());
    }

    @Test
    void testGetFavoriteInstruments_Success() throws Exception {
        FavoriteInstrument favoriteInstrument1 = mock(FavoriteInstrument.class);
        FavoriteInstrument favoriteInstrument2 = mock(FavoriteInstrument.class);

        when(instrumentsServiceMock.getFavorites()).thenReturn(CompletableFuture.completedFuture(List.of(favoriteInstrument1, favoriteInstrument2)));

        CompletableFuture<List<FavoriteInstrument>> futureResult = asyncTinkoffService.getFavoriteInstruments();
        List<FavoriteInstrument> result = futureResult.get();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPositionsById_Success() throws Exception {
        String accountId = "accountId1";
        SecurityPosition securityPosition1 = mock(SecurityPosition.class);
        SecurityPosition securityPosition2 = mock(SecurityPosition.class);

        Positions positionsMock = mock(Positions.class);
        when(positionsMock.getSecurities()).thenReturn(List.of(securityPosition1, securityPosition2));

        when(operationsServiceMock.getPositions(accountId)).thenReturn(CompletableFuture.completedFuture(positionsMock));

        CompletableFuture<List<SecurityPosition>> futureResult = asyncTinkoffService.getPositionsById(accountId);
        List<SecurityPosition> result = futureResult.get();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPositionsById_NotFound() {
        String accountId = "accountId1";

        Positions positionsMock = mock(Positions.class);
        when(positionsMock.getSecurities()).thenReturn(List.of());

        when(operationsServiceMock.getPositions(accountId)).thenReturn(CompletableFuture.completedFuture(positionsMock));

        CompletableFuture<List<SecurityPosition>> futureResult = asyncTinkoffService.getPositionsById(accountId);

        ExecutionException exception = assertThrows(ExecutionException.class, futureResult::get);

        Throwable cause = exception.getCause();

        assertInstanceOf(SecuritiesNotFoundException.class, cause, "Cause should be SecuritiesNotFoundException");

        assertEquals("Failed to retrieve positions.", cause.getMessage());
    }
}