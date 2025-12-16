package com.example.coupon.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStock(OutOfStockException e) {
        ErrorResponse body = new ErrorResponse("OUT_OF_STOCK", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT) // 409
                .body(body);
    }
}
