package flab.transtalk.auth.config;

import flab.transtalk.auth.exception.JwtAuthenticationException;
import flab.transtalk.auth.exception.OAuth2CustomAuthenticationException;
import flab.transtalk.auth.security.jwt.JwtAuthenticationEntryPoint;
import flab.transtalk.auth.security.jwt.JwtAuthenticationFilter;
import flab.transtalk.auth.security.oauth.OAuth2AuthenticationEntryPoint;
import flab.transtalk.auth.service.CustomOAuth2UserService;
import flab.transtalk.auth.security.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Value("#{'${spring.security.cors.allowed-origin-patterns}'.split(',')}")
    private List<String> allowedOriginPatterns;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**", "/ws-chat/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                );

        http.headers(h -> h.frameOptions(f -> f.sameOrigin()));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, e) -> {
                    if (e instanceof JwtAuthenticationException jwtEx) {
                        jwtAuthenticationEntryPoint.commence(request, response, jwtEx);
                    } else if (e instanceof OAuth2CustomAuthenticationException oauthEx){
                        oAuth2AuthenticationEntryPoint.commence(request, response, oauthEx);
                    } else {
                        log.error("Unhandled authentication exception", e);
                    }
                })
        );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        for (String allowedOriginPattern:allowedOriginPatterns){
            cfg.addAllowedOriginPattern(allowedOriginPattern.trim());
        }
        cfg.setAllowCredentials(true);
        cfg.addAllowedHeader(CorsConfiguration.ALL);
        cfg.addAllowedMethod(CorsConfiguration.ALL);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
