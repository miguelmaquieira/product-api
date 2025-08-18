package com.mgm.inditex.core.validation;

import java.io.Serial;
import java.util.List;

import com.mgm.inditex.shared.exception.ApiException;
import com.mgm.inditex.shared.exception.model.ApiError;
import com.mgm.inditex.shared.exception.model.ApiErrorType;

/**
 * Exception representing a 400 Bad Request scenario.
 *
 * <p>
 * Thrown when the request is syntactically valid but cannot be processed
 * due to client-side input issues, such as malformed data, unexpected values,
 * or unsupported data types.
 * </p>
 *
 * @author Miguel Maquieira
 */
public class ApiValidationException extends ApiException
{

    @Serial
    private static final long serialVersionUID = 2766537939905849585L;

    public ApiValidationException( final List<ApiError> errors )
    {
        super( errors, ApiErrorType.VALIDATION ); // Call super with errors list and specific ErrorType
    }

    public ApiValidationException( final String message, final ApiErrorType errorType )
    {
        super( message, errorType );
    }

    public ApiValidationException( final List<ApiError> errors, final ApiErrorType apiErrorType )
    {
        super( errors, ApiErrorType.valueOf( apiErrorType.name() ) );
    }

    public static ApiValidationException fromError( final ApiError error, final ApiErrorType errorType )
    {
        return ApiException.fromError( error, errorType, ApiValidationException::new );
    }
}
