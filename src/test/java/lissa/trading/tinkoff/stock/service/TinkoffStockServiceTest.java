package lissa.trading.tinkoff.stock.service;

import lissa.trading.tinkoff.stock.service.dto.stock.FigiesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StockPrice;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksPricesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.TickersDto;
import lissa.trading.tinkoff.stock.service.exception.StockNotFoundException;
import lissa.trading.tinkoff.stock.service.model.Currency;
import lissa.trading.tinkoff.stock.service.model.Stock;
import lissa.trading.tinkoff.stock.service.service.stock.TinkoffStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TinkoffStockServiceTest extends BaseTest {
    private TinkoffStockService tinkoffStockService;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        tinkoffStockService = new TinkoffStockService(asyncTinkoffServiceMock);
    }

    @Test
    void testGetStockByTicker_Success() {
        String ticker = "AAPL";
        Instrument instrumentMock = mock(Instrument.class);
        when(instrumentMock.getTicker()).thenReturn(ticker);
        when(instrumentMock.getFigi()).thenReturn("FIGI123");
        when(instrumentMock.getName()).thenReturn("Apple Inc.");
        when(instrumentMock.getInstrumentType()).thenReturn("Stock");
        when(instrumentMock.getCurrency()).thenReturn("USD");

        CompletableFuture<Optional<Instrument>> futureInstrument = CompletableFuture.completedFuture(Optional.of(instrumentMock));
        when(asyncTinkoffServiceMock.getInstrumentByTicker(ticker)).thenReturn(futureInstrument);

        Stock result = tinkoffStockService.getStockByTicker(ticker);

        assertNotNull(result);
        assertEquals(ticker, result.getTicker());
        assertEquals("FIGI123", result.getFigi());
        assertEquals("Apple Inc.", result.getName());
        assertEquals("Stock", result.getType());
        assertEquals(Currency.USD, result.getCurrency());
        assertEquals("TINKOFF", result.getSource());
    }

    @Test
    void testGetStockByTicker_NotFound() {
        String ticker = "UNKNOWN";
        CompletableFuture<Optional<Instrument>> futureInstrument = CompletableFuture.completedFuture(Optional.empty());
        when(asyncTinkoffServiceMock.getInstrumentByTicker(ticker)).thenReturn(futureInstrument);

        StockNotFoundException exception = assertThrows(StockNotFoundException.class, () -> {
            tinkoffStockService.getStockByTicker(ticker);
        });

        assertEquals("Stock not found for ticker: " + ticker, exception.getMessage());
    }

    @Test
    void testGetStocksByTickers_Success() {
        // Arrange
        String ticker1 = "AAPL";
        String ticker2 = "GOOGL";
        TickersDto tickersDto = new TickersDto(List.of(ticker1, ticker2));

        Instrument instrumentMock1 = mock(Instrument.class);
        when(instrumentMock1.getTicker()).thenReturn(ticker1);
        when(instrumentMock1.getFigi()).thenReturn("FIGI1");
        when(instrumentMock1.getName()).thenReturn("Apple Inc.");
        when(instrumentMock1.getInstrumentType()).thenReturn("Stock");
        when(instrumentMock1.getCurrency()).thenReturn("USD");

        Instrument instrumentMock2 = mock(Instrument.class);
        when(instrumentMock2.getTicker()).thenReturn(ticker2);
        when(instrumentMock2.getFigi()).thenReturn("FIGI2");
        when(instrumentMock2.getName()).thenReturn("Alphabet Inc.");
        when(instrumentMock2.getInstrumentType()).thenReturn("Stock");
        when(instrumentMock2.getCurrency()).thenReturn("USD");

        when(asyncTinkoffServiceMock.getInstrumentByTicker(ticker1))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(instrumentMock1)));

        when(asyncTinkoffServiceMock.getInstrumentByTicker(ticker2))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(instrumentMock2)));

        StocksDto result = tinkoffStockService.getStocksByTickers(tickersDto);

        assertNotNull(result);
        assertEquals(2, result.getStocks().size());

        Stock stock1 = result.getStocks().stream().filter(s -> s.getTicker().equals(ticker1)).findFirst().orElse(null);
        assertNotNull(stock1);
        assertEquals("FIGI1", stock1.getFigi());
        assertEquals("Apple Inc.", stock1.getName());
        assertEquals(Currency.USD, stock1.getCurrency());

        Stock stock2 = result.getStocks().stream().filter(s -> s.getTicker().equals(ticker2)).findFirst().orElse(null);
        assertNotNull(stock2);
        assertEquals("FIGI2", stock2.getFigi());
        assertEquals("Alphabet Inc.", stock2.getName());
        assertEquals(Currency.USD, stock2.getCurrency());
    }

    @Test
    void testGetPricesStocksByFigies_Success() {
        String figi1 = "FIGI1";
        String figi2 = "FIGI2";
        FigiesDto figiesDto = new FigiesDto(List.of(figi1, figi2));

        GetOrderBookResponse orderBookMock1 = mock(GetOrderBookResponse.class);
        Quotation lastPrice1 = Quotation.newBuilder()
                .setUnits(150)
                .setNano(500_000_000)
                .build();
        when(orderBookMock1.getFigi()).thenReturn(figi1);
        when(orderBookMock1.getLastPrice()).thenReturn(lastPrice1);

        GetOrderBookResponse orderBookMock2 = mock(GetOrderBookResponse.class);
        Quotation lastPrice2 = Quotation.newBuilder()
                .setUnits(2500)
                .setNano(0)
                .build();
        when(orderBookMock2.getFigi()).thenReturn(figi2);
        when(orderBookMock2.getLastPrice()).thenReturn(lastPrice2);

        when(asyncTinkoffServiceMock.getOrderBookByFigi(figi1))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(orderBookMock1)));

        when(asyncTinkoffServiceMock.getOrderBookByFigi(figi2))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(orderBookMock2)));

        StocksPricesDto result = tinkoffStockService.getPricesStocksByFigies(figiesDto);

        assertNotNull(result);
        assertEquals(2, result.getPrices().size());

        StockPrice price1 = result.getPrices().stream().filter(p -> p.getFigi().equals(figi1)).findFirst().orElse(null);
        assertNotNull(price1);
        assertEquals(150.5, price1.getPrice());

        StockPrice price2 = result.getPrices().stream().filter(p -> p.getFigi().equals(figi2)).findFirst().orElse(null);
        assertNotNull(price2);
        assertEquals(2500.0, price2.getPrice());
    }
}