package flab.transtalk.auth.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final String code;

    public JwtAuthenticationException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
