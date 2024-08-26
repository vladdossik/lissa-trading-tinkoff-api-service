package lissa.trading.tinkoff.stock.service.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FigiesDto {
    private List<String> figies;
}
