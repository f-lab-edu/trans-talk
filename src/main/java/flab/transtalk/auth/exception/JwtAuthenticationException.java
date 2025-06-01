package flab.transtalk.auth.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final JwtErrorCode errorCode;

    public JwtAuthenticationException(JwtErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
