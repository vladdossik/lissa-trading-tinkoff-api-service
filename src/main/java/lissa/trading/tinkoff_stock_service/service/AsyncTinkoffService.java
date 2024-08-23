package lissa.trading.tinkoff_stock_service.service;

import lissa.trading.tinkoff_stock_service.exception.SecuritiesNotFoundException;
import lissa.trading.tinkoff_stock_service.exception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.FavoriteInstrument;
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Positions;
import ru.tinkoff.piapi.core.models.SecurityPosition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTinkoffService {
    private final InvestApi investApi;

    public CompletableFuture<Instrument> getInstrumentByTicker(String ticker) {
        log.info("Getting {} from Tinkoff", ticker);
        return investApi.getInstrumentsService()
                .findInstrument(ticker)
                .thenCompose(instruments -> instruments.isEmpty()
                        ? CompletableFuture.failedFuture(new StockNotFoundException(String.format("Stock %s not found!", ticker)))
                        : investApi.getInstrumentsService().getInstrumentByFigi(instruments.get(0).getFigi()))
                .exceptionally(ex -> {
                    log.error("Failed to get instrument by ticker {}: {}", ticker, ex.getMessage());
                    throw new CompletionException(ex);
                });
    }

    public CompletableFuture<GetOrderBookResponse> getOrderBookByFigi(String figi) {
        log.info("Getting price {} from Tinkoff", figi);
        return investApi.getMarketDataService()
                .getOrderBook(figi, 1)
                .exceptionally(ex -> {
                    log.error("Failed to get order book by figi {}: {}", figi, ex.getMessage());
                    throw new CompletionException(ex);
                });
    }

    public CompletableFuture<List<FavoriteInstrument>> getFavoriteInstruments() {
        log.info("Getting favorite instruments from Tinkoff");
        return investApi.getInstrumentsService().getFavorites()
                .exceptionally(ex -> {
                    log.error("Failed to get favorite instruments: {}", ex.getMessage());
                    throw new CompletionException(ex);
                });
    }

    public CompletableFuture<List<SecurityPosition>> getPositionsById(String accountId) {
        log.info("Getting positions by accountDId {} from Tinkoff", accountId);
        return investApi.getOperationsService()
                .getPositions(accountId)
                .thenApply(Positions::getSecurities)
                .thenCompose(instruments -> instruments.isEmpty()
                        ? CompletableFuture.failedFuture(new SecuritiesNotFoundException(String.format("Positions for account %s not found!", accountId)))
                        : CompletableFuture.completedFuture(instruments))
                .exceptionally(ex -> {
                    log.error("Failed to get positions by accountId {}: {}", accountId, ex.getMessage());
                    throw new CompletionException(ex);
                });
    }
}
