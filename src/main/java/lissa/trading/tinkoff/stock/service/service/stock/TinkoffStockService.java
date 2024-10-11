package lissa.trading.tinkoff.stock.service.service.stock;

import lissa.trading.tinkoff.stock.service.dto.stock.FigiesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StockPrice;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksPricesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.TickersDto;
import lissa.trading.tinkoff.stock.service.exception.StockNotFoundException;
import lissa.trading.tinkoff.stock.service.model.Currency;
import lissa.trading.tinkoff.stock.service.model.Stock;
import lissa.trading.tinkoff.stock.service.service.AsyncTinkoffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Instrument;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TinkoffStockService implements StockService {
    private static final String SOURCE_NAME = "TINKOFF";
    private static final double NANO_DIVISOR = 1_000_000_000.0;

    private final AsyncTinkoffService asyncTinkoffService;

    @Override
    public Stock getStockByTicker(String ticker) {
        Optional<Instrument> optionalInstrument;
        try {
            optionalInstrument = asyncTinkoffService.getInstrumentByTicker(ticker).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while retrieving stock information for {}.", ticker, e);
            throw new StockNotFoundException("Failed to retrieve stock information due to thread interruption.", e);
        } catch (ExecutionException e) {
            log.error("Failed to retrieve stock information for {}.", ticker, e);
            throw new StockNotFoundException("Failed to retrieve stock information.", e);
        }

        return optionalInstrument.map(instrument -> new Stock(
                instrument.getTicker(),
                instrument.getFigi(),
                instrument.getName(),
                instrument.getInstrumentType(),
                Currency.getFromString(instrument.getCurrency()),
                SOURCE_NAME
        )).orElseThrow(() -> new StockNotFoundException("Stock not found for ticker: " + ticker));
    }

    @Override
    public StocksDto getStocksByTickers(TickersDto tickers) {
        List<CompletableFuture<Optional<Stock>>> stockFutures = tickers.getTickers().stream()
                .map(ticker -> asyncTinkoffService.getInstrumentByTicker(ticker)
                        .thenApply(optionalInstrument -> optionalInstrument.map(instrument -> new Stock(
                                instrument.getTicker(),
                                instrument.getFigi(),
                                instrument.getName(),
                                instrument.getInstrumentType(),
                                Currency.getFromString(instrument.getCurrency()),
                                SOURCE_NAME
                        ))))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                stockFutures.toArray(new CompletableFuture[0])
        );

        List<Stock> stocks = allFutures.thenApply(v ->
                stockFutures.stream()
                        .map(CompletableFuture::join)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList()
        ).join();

        return new StocksDto(stocks);
    }

    @Override
    public StocksPricesDto getPricesStocksByFigies(FigiesDto figiesDto) {
        List<CompletableFuture<Optional<StockPrice>>> priceFutures = figiesDto.getFigies().stream()
                .map(figi -> asyncTinkoffService.getOrderBookByFigi(figi)
                        .thenApply(optionalOrderBook -> optionalOrderBook.map(orderBook -> new StockPrice(
                                orderBook.getFigi(),
                                orderBook.getLastPrice().getUnits() +
                                        orderBook.getLastPrice().getNano() / NANO_DIVISOR
                        )))
                )
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                priceFutures.toArray(new CompletableFuture[0])
        );

        List<StockPrice> prices = allFutures.thenApply(v ->
                priceFutures.stream()
                        .map(CompletableFuture::join)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList()
        ).join();

        return new StocksPricesDto(prices);
    }
}
