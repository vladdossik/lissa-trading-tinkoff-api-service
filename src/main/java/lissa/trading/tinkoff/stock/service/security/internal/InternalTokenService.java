package lissa.trading.tinkoff.stock.service.security.internal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Getter
@Slf4j
@Component
public class InternalTokenService {

    @Value("${integration.rest.user-service.token}")
    private String userServiceInternalToken;

    @Value("${integration.rest.analytics-service.token}")
    private String analyticsServiceInternalToken;

    protected boolean validateInternalToken(String token) {
        log.info("Validating token {}", token);
        if (token.isEmpty()) {
            return false;
        }

        return userServiceInternalToken.equals(token) || analyticsServiceInternalToken.equals(token);
    }

    protected String getServiceNameFromToken(String token) {
        return token;
    }

    protected List<String> getRolesFromToken(String token) {
        return validateInternalToken(token)
                ? Collections.singletonList("ROLE_INTERNAL_SERVICE")
                : Collections.emptyList();
    }
}