package com.mgm.inditex.adapter.inbound.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mgm.inditex.core.validation.ApiValidationException;
import com.mgm.inditex.shared.exception.model.ApiError;
import com.mgm.inditex.shared.exception.model.ApiErrorResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the Inbound API.
 * Converts known exceptions into structured error responses.
 *
 * @author Miguel Maquieira
 */
@Slf4j
@ControllerAdvice
public class ApiControllerAdvise
{
    /**
     * Handles generic content-related exceptions not caught by more specific handlers.
     *
     * @param exception the content API exception
     * @return a 400 Bad Request response with a generic error message
     */
    @ExceptionHandler( { RuntimeException.class } )
    public ResponseEntity<ApiErrorResponse> handleContentApiException( final RuntimeException exception )
    {
        log.error( "Unhandled content exception", exception );

        var response = new ApiErrorResponse();
        response.setMessage( "An unexpected error occurred" );

        return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( response );
    }

    /**
     * Handles explicit bad request exceptions, often used for business logic errors.
     *
     * @param exception the API bad request exception containing specific error(s)
     * @return a 400 Bad Request response containing one or more error details
     */
    @ExceptionHandler( ApiValidationException.class )
    public ResponseEntity<ApiErrorResponse> handleApiBadRequest( final ApiValidationException exception )
    {
        List<ApiError> errors = exception.getErrors();
        logErrors( "Bad request error", errors, exception.getMessage() );

        var response = ApiErrorResponse.builder().message( exception.getMessage() ).errors( errors ).build();
        response.setMessage( exception.getMessage() );
        response.setErrors( errors );

        return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( response );
    }

    private void logErrors( final String prefix, final List<ApiError> errors, final String fallbackMessage )
    {
        if ( errors != null && !errors.isEmpty() )
        {
            var summary = errors.stream()
                .map( error -> String.format( "[key=%s, code=%s, message=%s]", error.getKey(), error.getErrorCode(),
                    error.getMessage() ) )
                .reduce( ( a, b ) -> a + ", " + b )
                .orElse( "" );

            log.error( "{} - {}", prefix, summary );
        }
        else
        {
            log.error( "{} - {}", prefix, fallbackMessage );
        }
    }
}
