package flab.transtalk.auth.security.jwt;

import flab.transtalk.auth.exception.JwtAuthenticationException;
import flab.transtalk.auth.exception.message.JwtExceptionMessages;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Jws<Claims> claimsJws = jwtTokenProvider.parseClaims(token);
                String userId = claimsJws.getBody().getSubject();
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (ExpiredJwtException e) {
                throw new JwtAuthenticationException(
                        "TOKEN_EXPIRED",
                        JwtExceptionMessages.TOKEN_EXPIRED, e);

            } catch (SignatureException e) {
                throw new JwtAuthenticationException(
                        "INVALID_SIGNATURE",
                        JwtExceptionMessages.INVALID_SIGNATURE, e);

            } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
                throw new JwtAuthenticationException(
                        "MALFORMED_TOKEN",
                        JwtExceptionMessages.MALFORMED_TOKEN, e);

            } catch (Exception e) {
                throw new JwtAuthenticationException(
                        "AUTH_FAILED",
                        JwtExceptionMessages.AUTH_FAILED, e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
