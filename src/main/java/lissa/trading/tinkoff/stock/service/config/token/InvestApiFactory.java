package lissa.trading.tinkoff.stock.service.config.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.core.InvestApi;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestApiFactory {

    private final ApiConfig apiConfig;
    private InvestApi currentInvestApi;

    public InvestApi createInvestApi() {
        String ssoToken = apiConfig.getTinkoffToken();

        if (ssoToken == null) {
            throw new IllegalStateException("Tinkoff token is not initialized");
        }

        if (Boolean.TRUE.equals(apiConfig.getIsSandBoxMode())) {
            log.debug("Creating Sandbox InvestApi with token: {}", ssoToken);
            currentInvestApi = InvestApi.createSandbox(ssoToken);
        } else {
            log.debug("Creating InvestApi with token: {}", ssoToken);
            currentInvestApi = InvestApi.create(ssoToken);
        }

        return currentInvestApi;
    }

    public void closeCurrentInvestApi() {
        if (currentInvestApi != null) {
            log.debug("Closing current InvestApi instance.");
            currentInvestApi.destroy(5); // Ждем до 5 секунд для завершения работы канала
            currentInvestApi = null;
        }
    }
}
