package com.fintech.kyc.repository;

import com.fintech.kyc.entity.KycProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access layer for KYC profiles.
 * Extends JpaRepository to inherit standard CRUD operations.
 */
@Repository
public interface KycProfileRepository extends JpaRepository<KycProfile, Long> {

    /** Lookup by business key (customerId), not surrogate PK. */
    Optional<KycProfile> findByCustomerId(String customerId);

    /** Duplicate guard before create. */
    boolean existsByCustomerId(String customerId);
}
