package lissa.trading.tinkoff.stock.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lissa.trading.lissa.auth.lib.dto.UpdateTinkoffTokenResponce;
import lissa.trading.tinkoff.stock.service.dto.account.AccountInfoDto;
import lissa.trading.tinkoff.stock.service.dto.account.BalanceDto;
import lissa.trading.tinkoff.stock.service.dto.account.FavouriteStocksDto;
import lissa.trading.tinkoff.stock.service.dto.account.MarginAttributesDto;
import lissa.trading.tinkoff.stock.service.dto.account.SecurityPositionsDto;
import lissa.trading.tinkoff.stock.service.dto.account.TinkoffTokenDto;
import lissa.trading.tinkoff.stock.service.dto.stock.FigiesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksDto;
import lissa.trading.tinkoff.stock.service.dto.stock.StocksPricesDto;
import lissa.trading.tinkoff.stock.service.dto.stock.TickersDto;
import lissa.trading.tinkoff.stock.service.model.Stock;
import lissa.trading.tinkoff.stock.service.security.jwt.AuthTokenFilter;
import lissa.trading.tinkoff.stock.service.service.account.AccountService;
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
@RequestMapping("/v1/internal")
@Tag(name = "Управление аккаунтом", description = "API для управления аккаунтом и портфелем")
public class InternalController {
    private final AuthTokenFilter authTokenFilter;
    private final AccountService tinkoffAccountService;
    private final StockService stockService;

    @Operation(summary = "Установить токен Тинькофф", description = "Сохраняет токен Тинькофф для авторизации.")
    @ApiResponse(description = "Токен Тинькофф успешно установлен")
    @PostMapping("/set-token")
    public UpdateTinkoffTokenResponce setTinkoffToken(@RequestBody TinkoffTokenDto tinkoffToken) {
        return authTokenFilter.updateTinkoffToken(tinkoffToken.getToken());
    }

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

    @Operation(summary = "Получить информацию о счете", description = "Возвращает информацию о счете пользователя.")
    @ApiResponse(description = "Информация о счете успешно получена",
            content = @Content(schema = @Schema(implementation = AccountInfoDto.class)))
    @GetMapping("/accounts")
    public AccountInfoDto getAccountsInfo() {
        return tinkoffAccountService.getAccountsInfo();
    }

    @Operation(summary = "Получить избранные акции", description = "Возвращает список избранных акций пользователя.")
    @ApiResponse(description = "Избранные акции успешно получены",
            content = @Content(schema = @Schema(implementation = FavouriteStocksDto.class)))
    @GetMapping("/favourites")
    public FavouriteStocksDto getFavouriteStocks() {
        return tinkoffAccountService.getFavouriteStocks();
    }

    @Operation(summary = "Получить баланс портфеля", description = "Возвращает баланс портфеля для указанного счета.")
    @ApiResponse(description = "Баланс портфеля успешно получен",
            content = @Content(schema = @Schema(implementation = BalanceDto.class)))
    @GetMapping("/portfolio/{accountId}")
    public BalanceDto getPortfolio(@PathVariable String accountId) {
        return tinkoffAccountService.getBalance(accountId);
    }

    @Operation(summary = "Получить маржинальные атрибуты", description = "Возвращает маржинальные атрибуты для указанного счета.")
    @ApiResponse(description = "Маржинальные атрибуты успешно получены",
            content = @Content(schema = @Schema(implementation = MarginAttributesDto.class)))
    @GetMapping("/margin/{accountId}")
    public MarginAttributesDto getMarginAttributes(@PathVariable String accountId) {
        return tinkoffAccountService.getMarginAttributes(accountId);
    }

    @Operation(summary = "Получить позиции по ценным бумагам", description = "Возвращает позиции по ценным бумагам для указанного счета.")
    @ApiResponse(description = "Позиции по ценным бумагам успешно получены",
            content = @Content(schema = @Schema(implementation = SecurityPositionsDto.class)))
    @GetMapping("/positions/{accountId}")
    public SecurityPositionsDto getPositionsById(@PathVariable String accountId) {
        return tinkoffAccountService.getPositionsById(accountId);
    }
}
