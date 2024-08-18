package lissa.trading.tinkoff_stock_service.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceDto {
    private String currency;
    private BigDecimal currentBalance;
    private BigDecimal totalAmountBalance;
}