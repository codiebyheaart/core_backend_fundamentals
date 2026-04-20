package com.fintech.kyc.service;

import com.fintech.kyc.dto.KycProfileRequest;
import com.fintech.kyc.dto.KycProfileResponse;
import com.fintech.kyc.entity.KycProfile;
import com.fintech.kyc.enums.KycStatus;
import com.fintech.kyc.exception.CustomerAlreadyExistsException;
import com.fintech.kyc.exception.CustomerNotFoundException;
import com.fintech.kyc.repository.KycProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core business logic for KYC profile management.
 *
 * Design decisions:
 * - No business logic leaks into the controller layer.
 * - Entity construction uses builder pattern for immutability clarity.
 * - @Transactional on write operations to guarantee atomicity.
 */
@Service
@RequiredArgsConstructor
public class KycProfileServiceImpl implements KycProfileService {

    private final KycProfileRepository kycProfileRepository;

    @Override
    @Transactional
    public KycProfileResponse createProfile(KycProfileRequest request) {

        if (kycProfileRepository.existsByCustomerId(request.getCustomerId())) {
            throw new CustomerAlreadyExistsException(request.getCustomerId());
        }

        KycProfile profile = KycProfile.builder()
                .customerId(request.getCustomerId())
                .fullName(request.getFullName())
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .kycStatus(KycStatus.PENDING)   // Default: always PENDING on creation
                .build();

        KycProfile saved = kycProfileRepository.save(profile);
        return KycProfileResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public KycProfileResponse getProfileByCustomerId(String customerId) {

        KycProfile profile = kycProfileRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return KycProfileResponse.from(profile);
    }
}
