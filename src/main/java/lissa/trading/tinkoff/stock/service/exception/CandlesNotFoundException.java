package lissa.trading.tinkoff.stock.service.exception;

public class CandlesNotFoundException extends RuntimeException {
    public CandlesNotFoundException(String message) {
        super(message);
    }
}
