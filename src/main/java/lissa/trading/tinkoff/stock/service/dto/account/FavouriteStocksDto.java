package lissa.trading.tinkoff.stock.service.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FavouriteStocksDto {
    List<String> favouriteStocks;
}
