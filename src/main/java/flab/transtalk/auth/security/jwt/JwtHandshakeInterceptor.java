package flab.transtalk.auth.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            String token = resolveToken(httpServletRequest);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return false;
        }

        String subject = jwtTokenProvider.getSubject(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        attributes.put("SPRING_SECURITY_CONTEXT", new SecurityContextImpl(authentication));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception == null) {
            log.info("WebSocket 핸드셰이크 성공: {}", request.getRemoteAddress());
        } else {
            log.warn("WebSocket 핸드셰이크 실패", exception);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getParameter("token");
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : null;
    }
}
