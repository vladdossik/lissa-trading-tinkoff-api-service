package lissa.trading.tinkoff.stock.service.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockPrice {
    private String figi;
    private Double price;
}
