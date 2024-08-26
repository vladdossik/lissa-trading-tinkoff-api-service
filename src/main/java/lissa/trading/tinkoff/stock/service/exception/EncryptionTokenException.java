package lissa.trading.tinkoff.stock.service.exception;

public class EncryptionTokenException extends RuntimeException {
    public EncryptionTokenException(String message) {
        super(message);
    }

    public EncryptionTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}