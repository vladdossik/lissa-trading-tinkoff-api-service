package lissa.trading.tinkoff.stock.service.service.stock;

import lissa.trading.tinkoff.stock.service.dto.stock.CandlesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.CompanyNamesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.FigiesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksPricesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.TickersDto;
import lissa.trading.tinkoff.stock.service.dto.stock.TinkoffCandlesRequestDto;
import lissa.trading.tinkoff.stock.service.model.Stock;

import java.util.List;

public interface StockService {
    Stock getStockByTicker(String ticker);

    StocksPricesDto getPricesStocksByFigies(FigiesDto figiesDto);

    StocksDto getStocksByTickers(TickersDto tickers);

    CandlesDto getCandles(TinkoffCandlesRequestDto candlesRequestDto);

    CompanyNamesDto getCompanyNamesByTickers(List<String> tickers);
}
