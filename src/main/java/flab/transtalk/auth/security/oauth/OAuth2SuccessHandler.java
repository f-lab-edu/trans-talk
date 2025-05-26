package flab.transtalk.auth.security.oauth;
import flab.transtalk.auth.domain.AuthProvider;
import flab.transtalk.auth.security.jwt.JwtTokenProvider;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.repository.UserRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.frontend.redirect-uri}")
    private String redirectUri;

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        AuthProvider provider;
        String nameAttributeKey;
        switch (registrationId) {
            case "google" -> {
                provider = AuthProvider.GOOGLE;
                nameAttributeKey = "sub";
            }
            default -> throw new IllegalStateException("미지원 소셜: " + registrationId);
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String providerId = (String) oAuth2User.getAttributes().get(nameAttributeKey);

        User user = userRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseThrow();

        String subject = user.getProvider() + ":" + user.getProviderId();
        String jwt = jwtTokenProvider.createToken(subject, DEFAULT_ROLE);

        String redirectUrl = UriComponentsBuilder.fromUriString(this.redirectUri)
                .queryParam("token", jwt)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
