package flab.transtalk.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OAuth2ErrorCode {

    USER_NOT_REGISTERED("USER_NOT_REGISTERED", "등록되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    PROVIDER_INFO_EXTRACTION_FAILED("PROVIDER_INFO_EXTRACTION_FAILED", "제공자 정보 추출에 실패하였습니다.", HttpStatus.BAD_REQUEST),
    AUTH_FAILED("AUTH_FAILED", "OAuth2 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus status;

    OAuth2ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
