package lissa.trading.tinkoff_stock_service.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FavoutiteStocksDto {
    List<String> favouriteStocksByTicker;
}
