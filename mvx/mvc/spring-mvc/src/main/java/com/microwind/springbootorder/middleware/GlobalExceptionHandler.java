package com.microwind.springbootorder.middleware;

import com.microwind.springbootorder.utils.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse() {
                    @Override
                    public HttpStatusCode getStatusCode() {
                        return null;
                    }

                    @Override
                    public ProblemDetail getBody() {
                        return null;
                    }
                });
    }
}