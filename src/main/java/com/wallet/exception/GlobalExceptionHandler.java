package com.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<?> handleInsufficientFunds(InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(errorBody(ex.getMessage(), 400));
    }

    @ExceptionHandler(FraudBlockedException.class)
    public ResponseEntity<?> handleFraudBlocked(FraudBlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(errorBody(ex.getMessage(), 403));
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<?> handleWalletNotFound(WalletNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(errorBody(ex.getMessage(), 404));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorBody("Something went wrong: " + ex.getMessage(), 500));
    }

    private Map<String, Object> errorBody(String message, int status) {
        return Map.of(
            "error", message,
            "status", status,
            "timestamp", LocalDateTime.now().toString()
        );
    }
}