package lissa.trading.tinkoff_stock_service.exception;

public class SecuritiesNotFoundException extends RuntimeException {
    public SecuritiesNotFoundException(String message) {
        super(message);
    }

    public SecuritiesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
