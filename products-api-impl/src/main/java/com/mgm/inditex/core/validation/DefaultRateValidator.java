package com.mgm.inditex.core.validation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.mgm.inditex.shared.exception.model.ApiError;

/**
 * Default implementation of the {@link RateValidator} interface.
 * <p>
 * This class provides concrete validation logic for price-related requests to the
 * core domain. It checks for the presence and validity of required parameters such as
 * {@code brandId}, {@code productId}, {@code date}, and {@code currency}.
 *
 * @author Miguel Maquieira
 */
@Component
public class DefaultRateValidator implements RateValidator
{
    private static final String VALIDATION_REQUIRED_ERROR_CODE = "validation.required";

    @Override
    public void validateGetPriceRequest( final Integer brandId, final Long productId, final String currency,
        final LocalDateTime date )
    {
        var errors = new ArrayList<ApiError>();

        validatePositiveNumber( "brandId", brandId, errors );
        validatePositiveNumber( "productId", productId, errors );
        validateNotNull(  "date", date, errors );
        validateNotNullOrEmpty( "currency", currency, errors );


        if ( !errors.isEmpty() )
        {
            throw new ApiValidationException( errors );
        }
    }

    private <T extends Number> void validatePositiveNumber( final String field, final T value,
        final List<ApiError> errors )
    {
        if ( Objects.isNull( value ) || value.longValue() < 0 )
        {
            errors.add( ApiError.builder()
                .key( field )
                .value( String.valueOf( value ) )
                .message( "'" + field + "' field can not be null or smaller than zero" )
                .errorCode( VALIDATION_REQUIRED_ERROR_CODE )
                .build() );
        }
    }

    private void validateNotNull( final String field, final Object value, final List<ApiError> errors )
    {
        if ( value == null )
        {
            errors.add( ApiError.builder()
                .key( field )
                .value( null )
                .message( "'" + field + "' field can not be null." )
                .errorCode( VALIDATION_REQUIRED_ERROR_CODE )
                .build() );
        }
    }

    private void validateNotNullOrEmpty( final String field, final String value, final List<ApiError> errors )
    {
        if ( value == null || value.trim().isEmpty() )
        {
            errors.add( ApiError.builder()
                .key( field )
                .value( value )
                .message( "'" + field + "' field can not be null or empty." )
                .errorCode( VALIDATION_REQUIRED_ERROR_CODE )
                .build() );
        }
    }
}
