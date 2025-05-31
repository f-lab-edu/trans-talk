package flab.transtalk.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum JwtErrorCode {

    TOKEN_EXPIRED    ("TOKEN_EXPIRED"   , "JWT 토큰이 만료되었습니다."      , HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE("INVALID_SIGNATURE", "JWT 서명이 올바르지 않습니다." , HttpStatus.UNAUTHORIZED),
    MALFORMED_TOKEN  ("MALFORMED_TOKEN" , "잘못된 JWT 형식입니다."         , HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND   ("USER_NOT_FOUND"  , "유효하지 않는 사용자입니다."     , HttpStatus.UNAUTHORIZED),
    AUTH_FAILED      ("AUTH_FAILED"     , "인증 실패"                     , HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus status;

    JwtErrorCode(String code, String message, HttpStatus status) {
        this.code    = code;
        this.message = message;
        this.status  = status;
    }
}
