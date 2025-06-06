package flab.transtalk.auth.domain;

import flab.transtalk.auth.exception.OAuth2CustomAuthenticationException;
import flab.transtalk.auth.exception.OAuth2ErrorCode;
import lombok.Getter;
import lombok.Value;
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
            default -> throw new OAuth2CustomAuthenticationException(OAuth2ErrorCode.PROVIDER_INFO_EXTRACTION_FAILED, null);
        }

        return new ProviderInfo(provider, providerId);
    }

    public String getNameAttributeKey() {
        switch (provider) {
            case GOOGLE -> {
                return "sub";
            }
            default -> throw new OAuth2CustomAuthenticationException(OAuth2ErrorCode.PROVIDER_INFO_EXTRACTION_FAILED, null);
        }
    }
}
