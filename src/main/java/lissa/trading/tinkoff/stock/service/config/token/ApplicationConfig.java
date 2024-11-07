package lissa.trading.tinkoff.stock.service.config.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.tinkoff.piapi.core.InvestApi;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfig {

    private final InvestApiFactory investApiFactory;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public InvestApi investApi() {
        return investApiFactory.createInvestApi();
    }
}