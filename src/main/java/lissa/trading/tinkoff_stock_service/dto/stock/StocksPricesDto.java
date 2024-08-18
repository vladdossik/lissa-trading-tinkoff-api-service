package lissa.trading.tinkoff_stock_service.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class StocksPricesDto {
    List<StockPrice> prices;
}
