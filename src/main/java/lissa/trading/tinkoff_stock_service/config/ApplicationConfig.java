package lissa.trading.tinkoff_stock_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.piapi.core.InvestApi;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final ApiConfig apiConfig;

    @Bean
    public InvestApi investApi() {
        String ssoToken = System.getenv("ssoToken");
        if (Boolean.TRUE.equals(apiConfig.getIsSandBoxMode())) {
            return InvestApi.createSandbox(ssoToken);
        } else {
            return InvestApi.create(ssoToken);
        }
    }
}
