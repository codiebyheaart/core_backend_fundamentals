package com.fintech.kyc.entity;

import com.fintech.kyc.enums.KycStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA entity representing a KYC profile record in the database.
 * Immutable ID + audit timestamp follow banking-grade design discipline.
 */
@Entity
@Table(name = "kyc_profiles")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business key – unique per customer across the system. */
    @Column(nullable = false, unique = true)
    private String customerId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String documentNumber;

    /** KYC lifecycle state. Defaults to PENDING on creation. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus kycStatus;

    /** Immutable audit timestamp set at record creation. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.kycStatus == null) {
            this.kycStatus = KycStatus.PENDING;
        }
    }
}
