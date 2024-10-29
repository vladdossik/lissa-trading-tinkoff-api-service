package lissa.trading.tinkoff.stock.service;

import lissa.trading.tinkoff.stock.service.service.AsyncTinkoffService;
import lissa.trading.tinkoff.stock.service.service.account.TinkoffAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.OperationsService;
import ru.tinkoff.piapi.core.UsersService;

public class BaseTest {
    protected InvestApi investApiMock;
    protected UsersService usersServiceMock;
    protected OperationsService operationsServiceMock;
    protected AsyncTinkoffService asyncTinkoffServiceMock;
    protected TinkoffAccountService tinkoffAccountService;

    @BeforeEach
    public void setUp() {
        investApiMock = Mockito.mock(InvestApi.class);
        usersServiceMock = Mockito.mock(UsersService.class);
        operationsServiceMock = Mockito.mock(OperationsService.class);
        asyncTinkoffServiceMock = Mockito.mock(AsyncTinkoffService.class);

        Mockito.when(investApiMock.getUserService()).thenReturn(usersServiceMock);
        Mockito.when(investApiMock.getOperationsService()).thenReturn(operationsServiceMock);

        tinkoffAccountService = new TinkoffAccountService(investApiMock, asyncTinkoffServiceMock);
    }
}