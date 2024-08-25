package lissa.trading.tinkoff.stock.service.config.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.core.InvestApi;

@Component
@Slf4j
public class InvestApiFactory {

    private final ApiConfig apiConfig;

    public InvestApiFactory(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    public InvestApi createInvestApi() {
        String ssoToken = apiConfig.getTinkoffToken();

        if (ssoToken == null) {
            throw new IllegalStateException("Tinkoff token is not initialized");
        }

        log.debug("Using Tinkoff token: {}", ssoToken);

        return Boolean.TRUE.equals(apiConfig.getIsSandBoxMode())
                ? InvestApi.createSandbox(ssoToken)
                : InvestApi.create(ssoToken);
    }
}
