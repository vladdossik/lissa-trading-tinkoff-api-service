package lissa.trading.tinkoff_stock_service.exception;

public class StockRetrievalException extends RuntimeException {
    public StockRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
