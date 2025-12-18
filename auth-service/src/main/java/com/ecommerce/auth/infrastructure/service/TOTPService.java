package com.ecommerce.auth.infrastructure.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

@Service
public class TOTPService {

    private final GoogleAuthenticator gAuth;

    public TOTPService() {
        this.gAuth = new GoogleAuthenticator();
    }

    public String generateSecret() {
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public boolean validateCode(String secret, int code) {
        return gAuth.authorize(secret, code);
    }

    public String generateQRCodeUrl(String email, String secret) {
        String issuer = "E-Commerce App";
        // Format: otpauth://totp/{issuer}:{email}?secret={secret}&issuer={issuer}
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer.replace(" ", "%20"),
                email,
                secret,
                issuer.replace(" ", "%20"));
    }
}
