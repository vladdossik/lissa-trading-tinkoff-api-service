package lissa.trading.tinkoff.stock.service.service;

import lissa.trading.tinkoff.stock.service.dto.stock.TinkoffCandlesRequestDto;
import lissa.trading.tinkoff.stock.service.exception.CandlesNotFoundException;
import lissa.trading.tinkoff.stock.service.exception.RetrieveFailedException;
import lissa.trading.tinkoff.stock.service.exception.SecuritiesNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.FavoriteInstrument;
import ru.tinkoff.piapi.contract.v1.FindInstrumentResponse;
import ru.tinkoff.piapi.contract.v1.GetCandlesResponse;
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse;
import ru.tinkoff.piapi.contract.v1.GetTechAnalysisResponse;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.InstrumentType;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Positions;
import ru.tinkoff.piapi.core.models.SecurityPosition;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTinkoffService {
    private final InvestApi investApi;

    public CompletableFuture<Optional<Instrument>> getInstrumentByTicker(String ticker) {
        log.info("Getting {} from Tinkoff", ticker);
        return investApi.getInstrumentsService()
                .findInstrument(ticker)
                .thenCompose(instruments ->
                        instruments.isEmpty()
                                ? CompletableFuture.completedFuture(Optional.<Instrument>empty())
                                : investApi.getInstrumentsService().getInstrumentByFigi(instruments.stream()
                                        .filter(instrumentShort -> instrumentShort.getInstrumentKind()
                                                .equals(InstrumentType.INSTRUMENT_TYPE_SHARE))
                                        .findFirst().get().getFigi())
                                .thenApply(Optional::of)
                )
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    if (ex instanceof TimeoutException) {
                        log.error("Timeout while getting instrument by ticker {}.", ticker);
                    } else {
                        log.error("Failed to get instrument by ticker {}: {}", ticker, ex.getMessage());
                    }
                    return Optional.empty();
                });
    }

    public CompletableFuture<Optional<GetOrderBookResponse>> getOrderBookByFigi(String figi) {
        log.info("Getting price for figi {} from Tinkoff", figi);
        return investApi.getMarketDataService()
                .getOrderBook(figi, 1)
                .thenApply(Optional::of)
                .exceptionally(ex -> {
                    log.error("Failed to get order book by figi {}: {}", figi, ex.getMessage());
                    return Optional.empty();
                });
    }

    public CompletableFuture<List<FavoriteInstrument>> getFavoriteInstruments() {
        log.info("Getting favorite instruments from Tinkoff");
        return investApi.getInstrumentsService().getFavorites()
                .exceptionally(ex -> {
                    log.error("Failed to get favorite instruments: {}", ex.getMessage());
                    throw new CompletionException(new RetrieveFailedException("Failed to retrieve favorite instruments.", ex));
                });
    }

    public CompletableFuture<List<SecurityPosition>> getPositionsById(String accountId) {
        log.info("Getting positions by accountId {} from Tinkoff", accountId);
        return investApi.getOperationsService()
                .getPositions(accountId)
                .thenApply(Positions::getSecurities)
                .thenCompose(securities ->
                        securities.isEmpty()
                                ? CompletableFuture.failedFuture(
                                new SecuritiesNotFoundException(String.format("Positions for account %s not found!", accountId)))
                                : CompletableFuture.completedFuture(securities)
                )
                .exceptionally(ex -> {
                    log.error("Failed to get positions by accountId {}: {}", accountId, ex.getMessage());
                    throw new CompletionException(new SecuritiesNotFoundException("Failed to retrieve positions.", ex));
                });
    }

    public CompletableFuture<List<HistoricCandle>> getCandles(TinkoffCandlesRequestDto tinkoffCandlesRequestDto) {
        log.info("Requesting historical candles from T-Bank");
        return investApi.getMarketDataService()
                .getCandles(tinkoffCandlesRequestDto.getInstrumentId(),
                        tinkoffCandlesRequestDto.getFrom().toInstant(),
                        tinkoffCandlesRequestDto.getTo().toInstant(),
                        tinkoffCandlesRequestDto.getInterval()
                )
                .thenCompose(candles ->
                    candles.isEmpty()
                            ? CompletableFuture.failedFuture(
                                    new CandlesNotFoundException("Failed to get historical candles from T-Bank"))
                            : CompletableFuture.completedFuture(candles)

                )
                .exceptionally(ex -> {
                    log.error("Failed to get historical candles from T-Bank by data: {}: {}", tinkoffCandlesRequestDto,
                            ex.getMessage());
                    throw new CompletionException(new CandlesNotFoundException(
                            "Failed to retrieve historical candies from T-Bank: " + ex.getMessage()));
                });
    }

}

