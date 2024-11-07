package lissa.trading.tinkoff.stock.service;

import lissa.trading.tinkoff.stock.service.service.AsyncTinkoffService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.ObjectProvider;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.OperationsService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseAsyncTest {
    protected InvestApi investApiMock;
    protected InstrumentsService instrumentsServiceMock;
    protected MarketDataService marketDataServiceMock;
    protected OperationsService operationsServiceMock;
    protected ObjectProvider<InvestApi> investApiProviderMock;
    protected AsyncTinkoffService asyncTinkoffService;

    @BeforeEach
    public void setUp() {
        investApiMock = mock(InvestApi.class);
        instrumentsServiceMock = mock(InstrumentsService.class);
        marketDataServiceMock = mock(MarketDataService.class);
        operationsServiceMock = mock(OperationsService.class);

        when(investApiMock.getInstrumentsService()).thenReturn(instrumentsServiceMock);
        when(investApiMock.getMarketDataService()).thenReturn(marketDataServiceMock);
        when(investApiMock.getOperationsService()).thenReturn(operationsServiceMock);

        investApiProviderMock = mock(ObjectProvider.class);
        when(investApiProviderMock.getObject()).thenReturn(investApiMock);

        asyncTinkoffService = new AsyncTinkoffService(investApiProviderMock);
    }
}