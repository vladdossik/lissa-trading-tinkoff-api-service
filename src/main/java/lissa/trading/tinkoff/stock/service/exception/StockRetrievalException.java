package lissa.trading.tinkoff.stock.service.exception;

public class StockRetrievalException extends RuntimeException {
    public StockRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
