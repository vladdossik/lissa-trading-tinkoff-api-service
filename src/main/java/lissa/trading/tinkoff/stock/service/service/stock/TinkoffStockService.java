package lissa.trading.tinkoff.stock.service.service.stock;

import lissa.trading.tinkoff.stock.service.service.AsyncTinkoffService;
import lissa.trading.tinkoff.stock.service.dto.stock.FigiesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StockPrice;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksPricesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.TickersDto;
import lissa.trading.tinkoff.stock.service.exception.StockNotFoundException;
import lissa.trading.tinkoff.stock.service.model.Currency;
import lissa.trading.tinkoff.stock.service.model.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Instrument;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TinkoffStockService implements StockService {
    private final AsyncTinkoffService asyncTinkoffService;

    @Override
    public Stock getStockByTicker(String ticker) {
        try {
            Instrument instrument = asyncTinkoffService.getInstrumentByTicker(ticker).get();

            return new Stock(
                    instrument.getTicker(),
                    instrument.getFigi(),
                    instrument.getName(),
                    instrument.getInstrumentType(),
                    Currency.getFromString(instrument.getCurrency()),
                    "TINKOFF"
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while retrieving stock information for {}.", ticker, e);
            throw new StockNotFoundException("Failed to retrieve stock information due to thread interruption.", e);
        } catch (ExecutionException e) {
            log.error("Failed to retrieve stock information for {}.", ticker, e);
            throw new StockNotFoundException("Failed to retrieve stock information.", e);
        }
    }

    @Override
    public StocksDto getStocksByTickers(TickersDto tickers) {
        List<CompletableFuture<Stock>> stockFutures = tickers.getTickers().stream()
                .map(asyncTinkoffService::getInstrumentByTicker)
                .map(future -> future.thenApply(instrument -> new Stock(
                        instrument.getTicker(),
                        instrument.getFigi(),
                        instrument.getName(),
                        instrument.getInstrumentType(),
                        Currency.getFromString(instrument.getCurrency()),
                        "TINKOFF"
                )))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                stockFutures.toArray(new CompletableFuture[0])
        );

        List<Stock> stocks = allFutures.thenApply(v ->
                stockFutures.stream()
                        .map(CompletableFuture::join)
                        .toList()
        ).join();

        return new StocksDto(stocks);
    }

    @Override
    public StocksPricesDto getPricesStocksByFigies(FigiesDto figiesDto) {
        List<CompletableFuture<StockPrice>> priceFutures = figiesDto.getFigies().stream()
                .map(asyncTinkoffService::getOrderBookByFigi)
                .map(future -> future.thenApply(orderBook -> new StockPrice(
                        orderBook.getFigi(),
                        orderBook.getLastPrice().getUnits() +
                                orderBook.getLastPrice().getNano() / 1_000_000_000.0)))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                priceFutures.toArray(new CompletableFuture[0])
        );

        List<StockPrice> prices = allFutures.thenApply(v ->
                priceFutures.stream()
                        .map(CompletableFuture::join)
                        .toList()
        ).join();

        return new StocksPricesDto(prices);
    }
}
