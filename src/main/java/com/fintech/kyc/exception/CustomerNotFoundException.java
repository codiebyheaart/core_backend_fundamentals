package com.fintech.kyc.exception;

/**
 * Thrown when a KYC profile lookup returns no result
 * for the given customerId.
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String customerId) {
        super("KYC profile not found for customerId: " + customerId);
    }
}
