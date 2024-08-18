package lissa.trading.tinkoff_stock_service.exception;

public class BalanceRetrievalException extends RuntimeException {
    public BalanceRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
