package lissa.trading.tinkoff.stock.service.security;

import lissa.trading.lissa.auth.lib.security.BaseAuthTokenFilter;
import lissa.trading.lissa.auth.lib.security.BaseWebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig extends BaseWebSecurityConfig {
    public WebSecurityConfig(BaseAuthTokenFilter authTokenFilter) {
        super(authTokenFilter);
    }
}