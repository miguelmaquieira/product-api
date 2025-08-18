package com.mgm.inditex.shared.exception.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a standardized error response structure for API operations.
 * This class typically encapsulates a general error message and a list of
 * more specific {@link ApiError} objects, providing detailed error information,
 * especially for validation failures.
 *
 * @author Miguel Maquieira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponse
{
    private String message;
    private List<ApiError> errors;
}
