package lissa.trading.tinkoff_stock_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;


@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
    @ExceptionHandler({StockNotFoundException.class})
    public ResponseEntity<ErrorDto> handleNotFound(Exception ex) {
        return new ResponseEntity<>(new ErrorDto(ex.getLocalizedMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({SecuritiesNotFoundException.class})
    public ResponseEntity<ErrorDto> handleSecuritiesNotFound(Exception ex) {
        return new ResponseEntity<>(new ErrorDto(ex.getLocalizedMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ApiRuntimeException.class})
    public ResponseEntity<ErrorDto> handleApiException(Exception ex) {
        return new ResponseEntity<>(new ErrorDto(ex.getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
