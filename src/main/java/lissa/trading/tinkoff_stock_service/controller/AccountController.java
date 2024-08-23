package lissa.trading.tinkoff_stock_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lissa.trading.tinkoff_stock_service.dto.account.AccountInfoDto;
import lissa.trading.tinkoff_stock_service.dto.account.FavouriteStocksDto;
import lissa.trading.tinkoff_stock_service.dto.account.MarginAttributesDto;
import lissa.trading.tinkoff_stock_service.dto.account.BalanceDto;
import lissa.trading.tinkoff_stock_service.dto.account.SecurityPositionsDto;
import lissa.trading.tinkoff_stock_service.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tinkoff/user")
@Tag(name = "Управление аккаунтом", description = "API для управления аккаунтом и портфелем")
public class AccountController {

    private final AccountService tinkoffAccountService;

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