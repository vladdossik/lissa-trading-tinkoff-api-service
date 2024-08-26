package lissa.trading.tinkoff.stock.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lissa.trading.tinkoff.stock.service.dto.stock.FigiesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksPricesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.TickersDto;
import lissa.trading.tinkoff.stock.service.model.Stock;
import lissa.trading.tinkoff.stock.service.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/tinkoff/stocks")
@Tag(name = "Управление акциями", description = "API для получения информации о акциях")
public class StockController {
    private final StockService stockService;

    @Operation(summary = "Получить информацию об акции", description = "Возвращает информацию об акции по тикеру.")
    @ApiResponse(description = "Информация об акции успешно получена",
            content = @Content(schema = @Schema(implementation = Stock.class)))
    @GetMapping("/{ticker}")
    public Stock getStock(@PathVariable String ticker) {
        return stockService.getStockByTicker(ticker);
    }

    @Operation(summary = "Получить информацию о нескольких акциях", description = "Возвращает информацию о нескольких акциях по переданным тикерам.")
    @ApiResponse(description = "Информация о нескольких акциях успешно получена",
            content = @Content(schema = @Schema(implementation = StocksDto.class)))
    @PostMapping("/getStocksByTickers")
    public StocksDto getStocksByTickers(@RequestBody TickersDto tickers) {
        return stockService.getStocksByTickers(tickers);
    }

    @Operation(summary = "Получить цены акций", description = "Возвращает текущие цены акций по переданным figies.")
    @ApiResponse(description = "Цены акций успешно получены",
            content = @Content(schema = @Schema(implementation = StocksPricesDto.class)))
    @PostMapping("/prices")
    public StocksPricesDto getPricesStocksByFigies(@RequestBody FigiesDto figiesDto) {
        return stockService.getPricesStocksByFigies(figiesDto);
    }
}