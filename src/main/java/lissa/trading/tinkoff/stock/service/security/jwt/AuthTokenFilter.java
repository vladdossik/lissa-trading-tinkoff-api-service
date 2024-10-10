package lissa.trading.tinkoff.stock.service.security.jwt;

import lissa.trading.lissa.auth.lib.dto.UpdateTinkoffTokenResponce;
import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.lissa.auth.lib.feign.AuthServiceClient;
import lissa.trading.lissa.auth.lib.security.BaseAuthTokenFilter;
import lissa.trading.lissa.auth.lib.security.EncryptionService;
import lissa.trading.tinkoff.stock.service.config.token.TokenUpdateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends BaseAuthTokenFilter<UserInfoDto> {

    private final AuthServiceClient authServiceClient;
    private final TokenUpdateService tokenUpdateService;

    @Override
    protected List<String> parseRoles(UserInfoDto userInfoDto) {
        return userInfoDto.getRoles();
    }

    @Override
    protected UserInfoDto retrieveUserInfo(String token) {
        try {
            return authServiceClient.getUserInfo("Bearer " + token);
        } catch (Exception ex) {
            log.error("Failed to retrieve user info from auth service: {}", ex.getMessage());
            return null;
        }
    }

    @Override
    protected String decodeTinkoffToken(UserInfoDto userInfoDto) {
        try {
            return EncryptionService.decrypt(userInfoDto.getTinkoffToken());
        } catch (Exception ex) {
            log.error("Failed to decrypt Tinkoff token: {}", ex.getMessage());
            return null;
        }
    }

    @Override
    public UpdateTinkoffTokenResponce updateTinkoffToken(String tinkoffToken) {
        log.debug("Updating Tinkoff token...");
        try {
            tokenUpdateService.updateToken(tinkoffToken);
            log.debug("Successfully updated Tinkoff token.");
            return new UpdateTinkoffTokenResponce("Successfully updated Tinkoff token.");
        } catch (Exception ex) {
            log.error("Failed to update Tinkoff token: {}", ex.getMessage());
            return new UpdateTinkoffTokenResponce("Tinkoff token was not updated.");
        }
    }
}