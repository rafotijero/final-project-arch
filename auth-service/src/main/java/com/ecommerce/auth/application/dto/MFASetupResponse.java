package com.ecommerce.auth.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MFASetupResponse {
    private String secret;
    private String qrCodeUrl;
}
