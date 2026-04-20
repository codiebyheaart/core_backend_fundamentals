package com.fintech.kyc.service;

import com.fintech.kyc.dto.KycProfileRequest;
import com.fintech.kyc.dto.KycProfileResponse;
import com.fintech.kyc.entity.KycProfile;
import com.fintech.kyc.enums.KycStatus;
import com.fintech.kyc.exception.CustomerAlreadyExistsException;
import com.fintech.kyc.exception.CustomerNotFoundException;
import com.fintech.kyc.repository.KycProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for KycProfileServiceImpl.
 * Uses Mockito to isolate service logic from the repository layer.
 */
@ExtendWith(MockitoExtension.class)
class KycProfileServiceImplTest {

    @Mock
    private KycProfileRepository kycProfileRepository;

    @InjectMocks
    private KycProfileServiceImpl kycProfileService;

    private KycProfileRequest validRequest;
    private KycProfile savedProfile;

    @BeforeEach
    void setUp() {
        validRequest = new KycProfileRequest();
        // Use reflection-style init via Lombok – fields are set through the no-arg constructor
        // For testing, we use a helper builder approach via the entity
        savedProfile = KycProfile.builder()
                .customerId("C001")
                .fullName("John Doe")
                .documentType("PASSPORT")
                .documentNumber("A1234567")
                .kycStatus(KycStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    //  CREATE PROFILE
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("createProfile: should save and return profile when customerId is unique")
    void createProfile_success() {
        when(kycProfileRepository.existsByCustomerId("C001")).thenReturn(false);
        when(kycProfileRepository.save(any(KycProfile.class))).thenReturn(savedProfile);

        // Build request manually (Lombok Getter but no Setter – set via test helper)
        KycProfileRequest req = buildRequest("C001", "John Doe", "PASSPORT", "A1234567");
        KycProfileResponse response = kycProfileService.createProfile(req);

        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo("C001");
        assertThat(response.getKycStatus()).isEqualTo(KycStatus.PENDING);
        verify(kycProfileRepository, times(1)).save(any(KycProfile.class));
    }

    @Test
    @DisplayName("createProfile: should throw CustomerAlreadyExistsException for duplicate customerId")
    void createProfile_duplicate_throwsConflict() {
        when(kycProfileRepository.existsByCustomerId("C001")).thenReturn(true);

        KycProfileRequest req = buildRequest("C001", "John Doe", "PASSPORT", "A1234567");

        assertThatThrownBy(() -> kycProfileService.createProfile(req))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessageContaining("C001");

        verify(kycProfileRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────
    //  GET PROFILE
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getProfileByCustomerId: should return profile when found")
    void getProfile_success() {
        when(kycProfileRepository.findByCustomerId("C001")).thenReturn(Optional.of(savedProfile));

        KycProfileResponse response = kycProfileService.getProfileByCustomerId("C001");

        assertThat(response.getCustomerId()).isEqualTo("C001");
        assertThat(response.getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("getProfileByCustomerId: should throw CustomerNotFoundException when not found")
    void getProfile_notFound_throwsException() {
        when(kycProfileRepository.findByCustomerId("X999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> kycProfileService.getProfileByCustomerId("X999"))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("X999");
    }

    // ─────────────────────────────────────────────────────────────
    //  Helper
    // ─────────────────────────────────────────────────────────────

    /** Constructs a KycProfileRequest using reflection to bypass Lombok @Getter-only fields. */
    private KycProfileRequest buildRequest(String customerId, String fullName,
                                            String documentType, String documentNumber) {
        try {
            KycProfileRequest req = new KycProfileRequest();
            setField(req, "customerId", customerId);
            setField(req, "fullName", fullName);
            setField(req, "documentType", documentType);
            setField(req, "documentNumber", documentNumber);
            return req;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build KycProfileRequest in test", e);
        }
    }

    private void setField(Object target, String fieldName, String value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
