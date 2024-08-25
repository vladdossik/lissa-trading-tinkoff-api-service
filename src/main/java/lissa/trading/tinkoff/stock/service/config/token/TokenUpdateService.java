package lissa.trading.tinkoff.stock.service.config.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.core.InvestApi;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenUpdateService {

    private final ApiConfig apiConfig;
    private final ConfigurableApplicationContext applicationContext;
    private final InvestApiFactory investApiFactory;

    private static final String INVEST_API_BEAN_NAME = "investApi";

    public void updateToken(String newToken) {
        apiConfig.setTinkoffToken(newToken);

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();

        if (registry.containsBeanDefinition(INVEST_API_BEAN_NAME)) {
            registry.removeBeanDefinition(INVEST_API_BEAN_NAME);
        }

        BeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(InvestApi.class, investApiFactory::createInvestApi)
                .getBeanDefinition();

        registry.registerBeanDefinition(INVEST_API_BEAN_NAME, beanDefinition);

        log.debug("New InvestApi created with token: {}", newToken);
    }
}
