package flab.transtalk.auth.domain;

import flab.transtalk.auth.exception.message.JwtExceptionMessages;
import flab.transtalk.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Value
@Getter
public class ProviderInfo {

    private final AuthProvider provider;
    private final String providerId;

    public static ProviderInfo fromOAuth2User(String registrationId, OAuth2User oAuth2User) {
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

        String providerId;
        switch (provider) {
            case GOOGLE -> providerId = oAuth2User.getAttribute("sub");
            default -> throw new OAuth2AuthenticationException(JwtExceptionMessages.AUTH_FAILED);
        }

        return new ProviderInfo(provider, providerId);
    }

    public String getNameAttributeKey() {
        switch (provider) {
            case GOOGLE -> {
                return "sub";
            }
            default -> throw new OAuth2AuthenticationException(JwtExceptionMessages.AUTH_FAILED);
        }
    }
}
