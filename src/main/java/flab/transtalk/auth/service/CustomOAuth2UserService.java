package flab.transtalk.auth.service;
import java.util.Collections;
import java.util.Map;

import flab.transtalk.auth.domain.ProviderInfo;
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

        ProviderInfo providerInfo = ProviderInfo.fromOAuth2User(registrationId, oAuth2User);
        String nameAttributeKey = providerInfo.getNameAttributeKey();
        String email = (String) oAuth2User.getAttribute("email");
        String name = (String) oAuth2User.getAttribute("name");

        User user = userRepository
                .findByProviderAndProviderId(providerInfo.getProvider(), providerInfo.getProviderId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .provider(providerInfo.getProvider())
                        .providerId(providerInfo.getProviderId())
                        .email(email)
                        .name(name)
                        .build()));

        Map<String, Object> exposed = Map.of(
                nameAttributeKey, providerInfo.getProviderId()
        );

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(DEFAULT_ROLE)),
                exposed,
                nameAttributeKey);
    }
}