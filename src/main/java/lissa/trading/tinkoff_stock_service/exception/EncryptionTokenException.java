package lissa.trading.tinkoff_stock_service.exception;

public class EncryptionTokenException extends RuntimeException {
    public EncryptionTokenException(String message) {
        super(message);
    }

    public EncryptionTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}