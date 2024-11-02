package lissa.trading.tinkoff.stock.service.config.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenUpdateService {

    private final ApiConfig apiConfig;
    private final InvestApiFactory investApiFactory;

    public void updateToken(String newToken) {
        log.debug("Updating Tinkoff token...");
        apiConfig.setTinkoffToken(newToken);

        // Закрываем текущий экземпляр InvestApi
        investApiFactory.closeCurrentInvestApi();

        log.debug("Tinkoff token updated to: {}", newToken);
    }
}
