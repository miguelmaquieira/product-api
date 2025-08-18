package com.mgm.inditex.shared.exception;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import com.mgm.inditex.shared.exception.model.ApiError;
import com.mgm.inditex.shared.exception.model.ApiErrorType;

import lombok.Getter;

/**
 * Abstract base class for all structured API exceptions.
 *
 * <p>
 * This exception is designed to carry a list of {@link ApiError} objects that provide
 * detailed, field-level or domain-specific error information. Subclasses such as
 * {@code BadRequestException}, {@code ConflictException}, and {@code UnauthorizedException}
 * should extend this class and represent specific HTTP error conditions.
 * </p>
 *
 * <p>
 * This class does not assume an HTTP status code directly, allowing controllers or
 * exception handlers to map exceptions to status codes explicitly, preserving separation
 * between domain logic and transport protocols.
 * </p>
 *
 * <p>
 * Typical usage:
 * <pre>{@code
 *   throw new BadRequestException(List.of(
 *     new Error("title", "", "REQUIRED_FIELD", "Title is required", null)
 *   ));
 * }</pre>
 *
 * @author Miguel Maquieira
 */
public abstract class ApiException extends RuntimeException
{

    @Serial
    private static final long serialVersionUID = 6139656031143413899L;
    private final List<ApiError> errors = new ArrayList<>();
    @Getter
    private final ApiErrorType errorType;


    /**
     * Constructs an exception from a single message string.
     * The message is wrapped as a default {@link ApiError} object.
     * The default {@link ApiErrorType} is {@code GENERIC}.
     *
     * @param message the general error message
     */
    protected ApiException( final String message )
    {
        super( message );
        this.errorType = ApiErrorType.GENERIC;
    }

    /**
     * Constructs an exception from a single message string and an {@link ApiErrorType}.
     * The message is wrapped as a default {@link ApiError} object.
     *
     * @param message the general error message
     * @param errorType the type of error, using the custom {@link ApiErrorType} enum
     */
    protected ApiException( final String message, final ApiErrorType errorType )
    {
        super( message );
        this.errorType = errorType;
    }

    /**
     * Constructs an exception with a list of detailed {@link ApiError} objects and an {@link ApiErrorType}.
     * The first error message from the list (if available) is typically used as the
     * root exception message.
     *
     * @param errors a list of structured error details
     * @param errorType the type of error, using the custom {@link ApiErrorType} enum
     */
    protected ApiException( final List<ApiError> errors, final ApiErrorType errorType )
    {
        if ( errors != null )
        {
            this.errors.addAll( errors );
        }
        this.errorType = errorType;
    }

    /**
     * Returns an unmodifiable list of structured errors associated with this exception.
     *
     * @return list of {@link ApiError} objects
     */
    public List<ApiError> getErrors()
    {
        return Collections.unmodifiableList( errors );
    }

    /**
     * Adds an error to the exception after construction.
     * Useful for aggregating validation issues incrementally.
     *
     * @param error the error to append
     */
    public void addError( final ApiError error )
    {
        if ( error != null )
        {
            this.errors.add( error );
        }
    }

    /**
     * Static factory method to create a new instance of a subclass of {@code ApiException}
     * from a single {@link ApiError} object.
     *
     * @param error       the error to encapsulate
     * @param errorType   the type of error, using the custom {@link ApiErrorType} enum
     * @param constructor a constructor reference (e.g., {@code BadRequestException::new})
     * @param <T>         the type of ApiException subclass to create
     * @return a new instance of the exception with the error attached
     */
    protected static <T extends ApiException> T fromError( final ApiError error,
        final ApiErrorType errorType, final BiFunction<List<ApiError>, ApiErrorType, T> constructor )
    {
        return constructor.apply( List.of( error ), errorType );
    }
}
