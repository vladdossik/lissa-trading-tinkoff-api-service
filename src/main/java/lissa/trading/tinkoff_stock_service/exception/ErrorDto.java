package lissa.trading.tinkoff_stock_service.exception;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ErrorDto {
    String error;
}
