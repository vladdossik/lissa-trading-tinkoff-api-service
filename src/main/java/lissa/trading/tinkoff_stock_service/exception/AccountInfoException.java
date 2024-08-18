package lissa.trading.tinkoff_stock_service.exception;

public class AccountInfoException extends RuntimeException {
    public AccountInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
