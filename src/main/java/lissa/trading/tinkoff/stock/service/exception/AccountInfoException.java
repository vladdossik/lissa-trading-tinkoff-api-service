package lissa.trading.tinkoff.stock.service.exception;

public class AccountInfoException extends RuntimeException {
    public AccountInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
