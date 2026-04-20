package com.fintech.kyc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Inbound payload for KYC profile creation.
 * Validation annotations enforce contract at the API boundary.
 */
@Getter
@NoArgsConstructor
public class KycProfileRequest {

    @NotBlank(message = "customerId is required")
    private String customerId;

    @NotBlank(message = "fullName is required")
    private String fullName;

    @NotBlank(message = "documentType is required")
    private String documentType;

    @NotBlank(message = "documentNumber is required")
    private String documentNumber;
}
