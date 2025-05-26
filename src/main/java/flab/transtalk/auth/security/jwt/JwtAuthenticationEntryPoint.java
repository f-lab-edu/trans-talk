package flab.transtalk.auth.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.transtalk.auth.exception.JwtAuthenticationException;
import flab.transtalk.common.dto.res.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper om;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {

        String code  = "AUTH_FAILED";
        String message   = "인증 실패";

        if (ex instanceof JwtAuthenticationException jwtEx) {
            code = jwtEx.getCode();
            message  = jwtEx.getMessage();
        }

        log.warn("[AUTH] {} - {}", code, message);

        ApiErrorResponse body = new ApiErrorResponse(code, List.of(message));

        response.setStatus(code.equals("MALFORMED_TOKEN") ? 400 : 401);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        om.writeValue(response.getWriter(), body);
    }
}
