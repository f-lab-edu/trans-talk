package flab.transtalk.auth.service;

import java.util.Collections;
import java.util.Map;
import flab.transtalk.auth.domain.AuthAccount;
import flab.transtalk.auth.domain.ProviderInfo;
import flab.transtalk.auth.repository.AuthAccountRepository;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.dto.req.ProfileCreateRequestDto;
import flab.transtalk.user.dto.req.UserCreateRequestDto;
import flab.transtalk.user.repository.UserRepository;
import flab.transtalk.user.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AuthAccountRepository authAccountRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        ProviderInfo providerInfo = ProviderInfo.fromOAuth2User(registrationId, oAuth2User);

        authAccountRepository
                .findByProviderAndProviderId(providerInfo.getProvider(), providerInfo.getProviderId())
                .orElseGet(() -> {
                    String email = (String) oAuth2User.getAttribute("email");
                    String name = (String) oAuth2User.getAttribute("name");

                    UserCreateRequestDto reqUserDto = UserCreateRequestDto.builder()
                            .email(email)
                            .build();
                    ProfileCreateRequestDto reqProfileDto = ProfileCreateRequestDto.builder()
                            .name(name)
                            .build();

                    Long userId = userService.signUp(reqUserDto, reqProfileDto);
                    User user = userRepository.findById(userId).orElseThrow(() -> {
                    });

                    return authAccountRepository.save(AuthAccount.builder()
                            .provider(providerInfo.getProvider())
                            .providerId(providerInfo.getProviderId())
                            .user(user)
                            .build());
                });

        String nameAttributeKey = providerInfo.getNameAttributeKey();
        Map<String, Object> exposed = Map.of(
                nameAttributeKey, providerInfo.getProviderId()
        );

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(DEFAULT_ROLE)),
                exposed,
                nameAttributeKey);
    }
}