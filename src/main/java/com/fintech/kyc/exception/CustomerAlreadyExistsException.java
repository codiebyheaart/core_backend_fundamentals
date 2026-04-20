package com.fintech.kyc.exception;

/**
 * Thrown when a KYC profile creation is attempted
 * for a customerId that already exists in the system.
 */
public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(String customerId) {
        super("KYC profile already exists for customerId: " + customerId);
    }
}
