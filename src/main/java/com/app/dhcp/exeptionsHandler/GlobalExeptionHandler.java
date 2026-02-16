package com.app.dhcp.exeptionsHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExeptionHandler {

    @ExceptionHandler(HandleException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(HandleException ex){
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("HttpStatusCode", ex.getHttpStatusCode());
        body.put("message",ex.getMessage());
        body.put("HttpStatus", ex.getHttpStatus());

        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(SQLIntegrityConstraintViolationException ex){
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("message",ex.getMessage());
        body.put("HttpStatusError", "CONFLICT");
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}
