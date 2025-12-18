package com.ecommerce.auth.application.exception;

public class MfaNotSetupException extends RuntimeException {
    public MfaNotSetupException() {
        super("MFA has not been set up for this user");
    }
}
