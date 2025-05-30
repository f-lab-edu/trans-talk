package flab.transtalk.auth.service;
import java.util.Collections;

import flab.transtalk.auth.domain.AuthProvider;
import flab.transtalk.auth.exception.message.JwtExceptionMessages;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String providerId;
        String nameAttributeKey;
        AuthProvider provider;
        switch (registrationId.toLowerCase()) {
            case "google" -> {
                provider = AuthProvider.GOOGLE;
                providerId = (String) oAuth2User.getAttribute("sub");
                nameAttributeKey = "sub";
            }
            default -> throw new OAuth2AuthenticationException(JwtExceptionMessages.AUTH_FAILED);
        }
        String email = (String) oAuth2User.getAttribute("email");
        String name = (String) oAuth2User.getAttribute("name");

        User user = userRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .provider(provider)
                        .providerId(providerId)
                        .email(email)
                        .name(name)
                        .build()));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(DEFAULT_ROLE)),
                oAuth2User.getAttributes(),
                nameAttributeKey);
    }
}
