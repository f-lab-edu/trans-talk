package flab.transtalk.auth.service;

import flab.transtalk.auth.exception.JwtAuthenticationException;
import flab.transtalk.auth.exception.JwtErrorCode;
import flab.transtalk.auth.security.principal.CustomUserDetails;
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
    public UserDetails loadUserByUsername(String externalId) throws UsernameNotFoundException {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new JwtAuthenticationException(JwtErrorCode.USER_NOT_FOUND, null));

        return CustomUserDetails.builder()
                .userId(user.getId())
                .username(externalId)
                .authorities(Collections.singleton(new SimpleGrantedAuthority(DEFAULT_ROLE)))
                .build();
    }
}
