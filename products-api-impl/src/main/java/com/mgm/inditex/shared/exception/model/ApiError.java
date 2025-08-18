package com.mgm.inditex.shared.exception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a field-level or domain-level error.
 *
 * @author Miguel Maquieira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError
{
    /**
     * The field or property that caused the error (e.g., "title").
     */
    private String key;

    /**
     * The value that triggered the error (e.g., "abc123").
     */
    private String value;

    /**
     * Programmatic error code (e.g., "INVALID_FORMAT", "REQUIRED_FIELD").
     */
    private String errorCode;

    /**
     * Human-readable error message (for users or developers).
     */
    private String message;

    /**
     * Optional detailed message for internal logs or diagnostics.
     */
    private String debugMessage;
}
