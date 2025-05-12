package com.ecfr.exception;

public class ECfrApiException extends RuntimeException {
    
    public ECfrApiException(String message) {
        super(message);
    }

    public ECfrApiException(String message, Throwable cause) {
        super(message, cause);
    }
} 