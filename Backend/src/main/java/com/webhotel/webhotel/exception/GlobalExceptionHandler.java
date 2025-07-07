package com.webhotel.webhotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.webhotel.webhotel.dto.ErrorDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (message != null && message.contains("booked for the selected dates")) {
            status = HttpStatus.CONFLICT;
        }

        return ResponseEntity.status(status).body(new ErrorDto(message));
    }
}
