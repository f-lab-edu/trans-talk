package flab.transtalk.common.exception.handler;

import com.nimbusds.jose.JOSEException;
import flab.transtalk.common.dto.res.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler {
    @ExceptionHandler(JOSEException.class)
    public ResponseEntity<ApiErrorResponse> handleJOSEException(JOSEException e) {
        log.error("Signed Cookie 발급 중 JOSEException 발생", e);

        List<String> errors = List.of(
                "Signed Cookie 발급 중 내부 오류가 발생했습니다.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("SIGNED_COOKIE_ERROR", errors));
    }
}
