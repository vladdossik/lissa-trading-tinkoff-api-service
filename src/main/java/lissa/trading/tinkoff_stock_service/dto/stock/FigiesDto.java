package lissa.trading.tinkoff_stock_service.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FigiesDto {
    private List<String> figies;
}
