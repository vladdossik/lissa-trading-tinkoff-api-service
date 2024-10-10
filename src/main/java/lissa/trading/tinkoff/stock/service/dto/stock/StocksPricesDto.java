package lissa.trading.tinkoff.stock.service.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StocksPricesDto {
    private List<StockPrice> prices;
}
