package flab.transtalk.auth.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class OAuth2CustomAuthenticationException extends AuthenticationException {

    private final OAuth2ErrorCode errorCode;

    public OAuth2CustomAuthenticationException(OAuth2ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
