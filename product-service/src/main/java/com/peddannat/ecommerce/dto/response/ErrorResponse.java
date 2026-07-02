package com.peddannat.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Structured error response for validation and exception handling.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;
}