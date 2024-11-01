package lissa.trading.tinkoff.stock.service.dto.stock;

import lissa.trading.tinkoff.stock.service.model.Candle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandlesDto {
    private List<Candle> candles;
}
