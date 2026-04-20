package com.fintech.kyc.dto;

import com.fintech.kyc.entity.KycProfile;
import com.fintech.kyc.enums.KycStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Outbound response payload for KYC profile reads.
 * Constructed from the entity – keeps entity details off the wire.
 */
@Getter
@Builder
public class KycProfileResponse {

    private String customerId;
    private String fullName;
    private String documentType;
    private String documentNumber;
    private KycStatus kycStatus;
    private LocalDateTime createdAt;

    /** Factory method: maps entity → response DTO cleanly. */
    public static KycProfileResponse from(KycProfile profile) {
        return KycProfileResponse.builder()
                .customerId(profile.getCustomerId())
                .fullName(profile.getFullName())
                .documentType(profile.getDocumentType())
                .documentNumber(profile.getDocumentNumber())
                .kycStatus(profile.getKycStatus())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
