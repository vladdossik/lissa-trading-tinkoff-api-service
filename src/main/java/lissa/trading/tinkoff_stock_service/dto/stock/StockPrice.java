package lissa.trading.tinkoff_stock_service.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class StockPrice {
    String figi;
    Double price;
}
