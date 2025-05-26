package flab.transtalk.auth.exception.message;

public class JwtExceptionMessages {
    public static final String TOKEN_EXPIRED     = "JWT 토큰이 만료되었습니다.";
    public static final String INVALID_SIGNATURE = "JWT 서명이 올바르지 않습니다.";
    public static final String MALFORMED_TOKEN   = "잘못된 JWT 토큰 형식입니다.";
    public static final String AUTH_FAILED       = "JWT 인증 처리 중 오류가 발생했습니다.";
}
