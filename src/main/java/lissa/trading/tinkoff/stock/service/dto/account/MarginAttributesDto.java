package lissa.trading.tinkoff.stock.service.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class MarginAttributesDto {
    private String currency;
    private Double liquidPortfolio;
}