package lissa.trading.tinkoff.stock.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candle {
    private long volume;
    private Double open;
    private Double close;
    private Double high;
    private Double low;
    private OffsetDateTime time;
    private boolean isComplete;
}
