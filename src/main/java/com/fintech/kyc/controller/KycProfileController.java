package com.fintech.kyc.controller;

import com.fintech.kyc.dto.KycProfileRequest;
import com.fintech.kyc.dto.KycProfileResponse;
import com.fintech.kyc.service.KycProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing KYC Profile API endpoints.
 *
 * Responsibilities:
 * - Accept HTTP requests and delegate to service layer.
 * - Return appropriate HTTP status codes.
 * - No business logic here – thin controller, fat service.
 */
@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycProfileController {

    private final KycProfileService kycProfileService;

    /**
     * POST /api/kyc
     * Creates a new KYC profile.
     * Returns 201 Created on success, 409 Conflict if customerId exists,
     * 400 Bad Request if validation fails.
     */
    @PostMapping
    public ResponseEntity<KycProfileResponse> createProfile(
            @Valid @RequestBody KycProfileRequest request) {

        KycProfileResponse response = kycProfileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/kyc/{customerId}
     * Retrieves a KYC profile by customerId.
     * Returns 200 OK on success, 404 Not Found if not present.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<KycProfileResponse> getProfile(
            @PathVariable String customerId) {

        KycProfileResponse response = kycProfileService.getProfileByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }
}
