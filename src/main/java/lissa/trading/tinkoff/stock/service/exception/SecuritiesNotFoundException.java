package lissa.trading.tinkoff.stock.service.exception;

public class SecuritiesNotFoundException extends RuntimeException {
    public SecuritiesNotFoundException(String message) {
        super(message);
    }

    public SecuritiesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
