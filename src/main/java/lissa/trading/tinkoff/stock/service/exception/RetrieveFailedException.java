package lissa.trading.tinkoff.stock.service.exception;

public class RetrieveFailedException extends RuntimeException {
    public RetrieveFailedException(String message) {
        super(message);
    }

    public RetrieveFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
