package com.fintech.kyc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Standard error envelope returned for all error scenarios.
 * Ensures predictable error structure for API consumers.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
