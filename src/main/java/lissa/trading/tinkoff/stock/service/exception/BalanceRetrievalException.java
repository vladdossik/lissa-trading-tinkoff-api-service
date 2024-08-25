package lissa.trading.tinkoff.stock.service.exception;

public class BalanceRetrievalException extends RuntimeException {
    public BalanceRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
