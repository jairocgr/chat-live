package io.chat.live.spring;

import io.chat.live.exception.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<Object> handle(BusinessException ex) {
        return ResponseEntity.status(BAD_REQUEST).body(toError(ex));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handle(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(BAD_REQUEST).body(toError(ex));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handle(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(BAD_REQUEST).body(toError(ex));
    }

    private ErrorResponse toError(HttpMessageNotReadableException ex) {
        return new ErrorResponse("MALFORMED_REQUEST", ex.getMessage());
    }

    private ValidationErrorResponse toError(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ValidationErrorResponse("VALIDATION", "Invalid arguments", errors);
    }

    private ErrorResponse toError(BusinessException ex) {
        return new ErrorResponse(toErrCode(ex), ex.getMessage());
    }

    private String toErrCode(Exception ex) {
        var name = ex.getClass().getSimpleName();
        var code = name.replace("Exception", "");
        return camelToUppercaseSnake(code);
    }

    private String camelToUppercaseSnake(String str) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return str.replaceAll(regex, replacement)
            .toUpperCase();
    }

    record ErrorResponse(String code, String message) {}

    record ValidationErrorResponse(String code, String message, Map<String, String> errors) {}
}
