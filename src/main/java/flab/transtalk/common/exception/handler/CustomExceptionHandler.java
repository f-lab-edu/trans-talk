package flab.transtalk.common.exception.handler;

import flab.transtalk.common.dto.res.ApiErrorResponse;
import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException e){
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        errors.add("inputIds: "+e.getInputIds());
        errors.add("foundIds: "+e.getFoundIds());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse("NOT_FOUND", errors));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handlerBadRequestException(BadRequestException e){
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("BAD_REQUEST", errors));
    }
}
