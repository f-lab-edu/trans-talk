package flab.transtalk.auth.security.oauth;
import flab.transtalk.auth.domain.AuthAccount;
import flab.transtalk.auth.domain.ProviderInfo;
import flab.transtalk.auth.exception.JwtAuthenticationException;
import flab.transtalk.auth.exception.JwtErrorCode;
import flab.transtalk.auth.repository.AuthAccountRepository;
import flab.transtalk.auth.security.jwt.JwtTokenProvider;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthAccountRepository authAccountRepository;

    @Value("${spring.security.oauth2.frontend.redirect-uri}")
    private String redirectUri;

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    @Transactional(readOnly = true)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        ProviderInfo providerInfo = ProviderInfo.fromOAuth2User(registrationId, oAuth2User);

        AuthAccount acct = authAccountRepository
                .findByProviderAndProviderId(providerInfo.getProvider(), providerInfo.getProviderId())
                .orElseThrow(() -> {
                    throw new JwtAuthenticationException(JwtErrorCode.AUTH_FAILED, null);
                });

        String subject = acct.getUser().getExternalId();
        String jwt = jwtTokenProvider.createToken(subject, DEFAULT_ROLE);

        String redirectUrl = UriComponentsBuilder.fromUriString(this.redirectUri)
                .queryParam("token", jwt)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
