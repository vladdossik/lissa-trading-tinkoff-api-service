package lissa.trading.tinkoff_stock_service.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SecurityPositionsDto {
    private List<SecurityPosition> positions;
}
