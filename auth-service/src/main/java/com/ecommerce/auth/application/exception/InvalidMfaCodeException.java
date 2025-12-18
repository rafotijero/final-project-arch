package com.ecommerce.auth.application.exception;

public class InvalidMfaCodeException extends RuntimeException {
    public InvalidMfaCodeException(String message) {
        super(message);
    }

    public InvalidMfaCodeException() {
        super("Invalid MFA code provided");
    }
}
