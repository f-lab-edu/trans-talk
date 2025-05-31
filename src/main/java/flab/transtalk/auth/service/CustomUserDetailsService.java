package flab.transtalk.auth.service;

import flab.transtalk.auth.domain.ProviderInfo;
import flab.transtalk.auth.exception.message.JwtExceptionMessages;
import flab.transtalk.auth.security.principal.CustomUserDetails;
import flab.transtalk.auth.domain.AuthProvider;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        ProviderInfo providerInfo = ProviderInfo.fromSubject(subject);

        User user = userRepository
                .findByProviderAndProviderId(providerInfo.getProvider(), providerInfo.getProviderId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + subject));

        // JWT subject를 username으로 사용
        return toUserDetails(user.getId(), subject);
    }

    private UserDetails toUserDetails(long userId, String username) {
        return CustomUserDetails.builder()
                .userId(userId)
                .username(username)
                .authorities(Collections.singleton(new SimpleGrantedAuthority(DEFAULT_ROLE)))
                .build();
    }
}
