package lissa.trading.tinkoff_stock_service.security;

import lissa.trading.auth_security_lib.dto.UserInfoDto;
import lissa.trading.auth_security_lib.feign.AuthServiceClient;
import lissa.trading.auth_security_lib.security.BaseAuthTokenFilter;
import lissa.trading.auth_security_lib.security.EncryptionService;
import lissa.trading.tinkoff_stock_service.config.token.TokenUpdateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends BaseAuthTokenFilter {

    private final AuthServiceClient authServiceClient;
    private final TokenUpdateService tokenUpdateService;

    @Override
    protected List<String> parseRoles(Object userInfo) {
        if (userInfo instanceof UserInfoDto userInfoDto) {
            return userInfoDto.getRoles();
        } else {
            log.warn("Invalid user info object type: {}", userInfo.getClass().getName());
            return Collections.emptyList();
        }
    }

    @Override
    protected Object retrieveUserInfo(String token) {
        try {
            return authServiceClient.getUserInfo("Bearer " + token);
        } catch (Exception ex) {
            log.error("Failed to retrieve user info from auth service: {}", ex.getMessage());
            return null;
        }
    }

    @Override
    protected String decodeTinkoffToken(Object userInfo) {
        if (userInfo instanceof UserInfoDto userInfoDto) {
            try {
                return EncryptionService.decrypt(userInfoDto.getTinkoffToken());
            } catch (Exception ex) {
                log.error("Failed to decrypt Tinkoff token: {}", ex.getMessage());
                return null;
            }
        } else {
            log.warn("Invalid user info object type for decoding Tinkoff token: {}", userInfo.getClass().getName());
            return null;
        }
    }

    @Override
    protected void updateTinkoffToken(String tinkoffToken) {
        try {
            tokenUpdateService.updateToken(tinkoffToken);
            log.debug("Successfully updated Tinkoff token.");
        } catch (Exception ex) {
            log.error("Failed to update Tinkoff token: {}", ex.getMessage());
        }
    }
}