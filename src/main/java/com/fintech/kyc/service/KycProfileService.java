package com.fintech.kyc.service;

import com.fintech.kyc.dto.KycProfileRequest;
import com.fintech.kyc.dto.KycProfileResponse;

/**
 * Contract for KYC profile business operations.
 * Programming to an interface keeps the controller
 * decoupled from the implementation.
 */
public interface KycProfileService {

    /**
     * Creates a new KYC profile.
     *
     * @param request validated inbound payload
     * @return the persisted profile as a response DTO
     * @throws com.fintech.kyc.exception.CustomerAlreadyExistsException if customerId is taken
     */
    KycProfileResponse createProfile(KycProfileRequest request);

    /**
     * Retrieves a KYC profile by its business key.
     *
     * @param customerId the unique customer identifier
     * @return the matching profile as a response DTO
     * @throws com.fintech.kyc.exception.CustomerNotFoundException if no profile exists
     */
    KycProfileResponse getProfileByCustomerId(String customerId);
}
