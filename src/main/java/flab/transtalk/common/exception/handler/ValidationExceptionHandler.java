package flab.transtalk.common.exception.handler;

import flab.transtalk.common.dto.res.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(err->err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new ApiErrorResponse("VALIDATION_ERROR", errors));
    }
}
