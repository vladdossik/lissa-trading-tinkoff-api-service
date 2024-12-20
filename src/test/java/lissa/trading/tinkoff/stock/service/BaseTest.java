package lissa.trading.tinkoff.stock.service;

import lissa.trading.tinkoff.stock.service.service.AsyncTinkoffService;
import lissa.trading.tinkoff.stock.service.service.account.TinkoffAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.ObjectProvider;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.OperationsService;
import ru.tinkoff.piapi.core.UsersService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTest {
    protected InvestApi investApiMock;
    protected UsersService usersServiceMock;
    protected OperationsService operationsServiceMock;
    protected ObjectProvider<InvestApi> investApiProviderMock;
    protected AsyncTinkoffService asyncTinkoffServiceMock;
    protected TinkoffAccountService tinkoffAccountService;

    @BeforeEach
    public void setUp() {
        investApiMock = mock(InvestApi.class);
        usersServiceMock = mock(UsersService.class);
        operationsServiceMock = mock(OperationsService.class);

        when(investApiMock.getUserService()).thenReturn(usersServiceMock);
        when(investApiMock.getOperationsService()).thenReturn(operationsServiceMock);

        investApiProviderMock = mock(ObjectProvider.class);
        when(investApiProviderMock.getObject()).thenReturn(investApiMock);

        asyncTinkoffServiceMock = mock(AsyncTinkoffService.class);

        tinkoffAccountService = new TinkoffAccountService(investApiProviderMock, asyncTinkoffServiceMock);
    }
}
