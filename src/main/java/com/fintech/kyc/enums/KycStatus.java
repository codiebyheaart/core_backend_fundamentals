package com.fintech.kyc.enums;

/**
 * Represents the lifecycle status of a KYC verification record.
 * Banking discipline: statuses are immutable once set to VERIFIED/EXPIRED.
 */
public enum KycStatus {

    /** Initial state – document received, not yet reviewed. */
    PENDING,

    /** Document validated successfully by compliance team. */
    VERIFIED,

    /** Document or verification has lapsed / expired. */
    EXPIRED
}
