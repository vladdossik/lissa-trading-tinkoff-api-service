package lissa.trading.tinkoff_stock_service.service.stock;

import lissa.trading.tinkoff_stock_service.dto.stock.FigiesDto;
import lissa.trading.tinkoff_stock_service.dto.stock.StocksDto;
import lissa.trading.tinkoff_stock_service.dto.stock.StocksPricesDto;
import lissa.trading.tinkoff_stock_service.dto.stock.TickersDto;
import lissa.trading.tinkoff_stock_service.model.Stock;

public interface StockService {
    Stock getStockByTicker(String ticker);

    StocksPricesDto getPricesStocksByFigies(FigiesDto figiesDto);

    StocksDto getStocksByTickers(TickersDto tickers);
}
