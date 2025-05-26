package flab.transtalk.auth.service;

import flab.transtalk.auth.security.principal.CustomUserDetails;
import flab.transtalk.auth.domain.AuthProvider;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!username.contains(":")) {
            throw new UsernameNotFoundException("Invalid token subject: " + username);
        }
        String[] parts = username.split(":", 2);
        AuthProvider provider;
        try {
            provider = AuthProvider.valueOf(parts[0]);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Unsupported provider: " + parts[0]);
        }

        User user = userRepository
                .findByProviderAndProviderId(provider, parts[1])
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // JWT subject를 username으로 사용
        return toUserDetails(user.getId(), username);
    }

    private UserDetails toUserDetails(long userId, String username) {
        return CustomUserDetails.builder()
                .userId(userId)
                .username(username)
                .authorities(Collections.singleton(new SimpleGrantedAuthority(DEFAULT_ROLE)))
                .build();
    }
}
