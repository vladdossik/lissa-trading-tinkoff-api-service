package lissa.trading.tinkoff.stock.service.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TinkoffCandlesRequestDto {
    private String instrumentId;
    private OffsetDateTime from;
    private OffsetDateTime to;
    private CandleInterval interval;
}
