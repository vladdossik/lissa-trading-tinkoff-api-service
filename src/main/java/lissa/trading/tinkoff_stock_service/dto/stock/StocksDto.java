package lissa.trading.tinkoff_stock_service.dto.stock;

import lissa.trading.tinkoff_stock_service.model.Stock;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StocksDto {
    List<Stock> stocks;
}
