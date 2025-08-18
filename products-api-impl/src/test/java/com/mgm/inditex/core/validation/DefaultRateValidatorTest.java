package com.mgm.inditex.core.validation;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.mgm.inditex.shared.exception.model.ApiError;

// CSOFF: Javadoc
class DefaultRateValidatorTest
{
    private static final long PRODUCT_ID = 35455L;
    private static final int BRAND_ID = 1;
    private static final String CURRENCY = "EUR";
    private static final LocalDateTime DATE = LocalDateTime.of( 2020, 6, 14, 0, 0 );

    private DefaultRateValidator cut;

    @BeforeEach
    void setUp()
    {
        cut = new DefaultRateValidator();
    }

    @Test
    void validateGetPriceRequestWhenValidParamsShouldNotThrowException()
    {
        assertDoesNotThrow( () -> cut.validateGetPriceRequest( BRAND_ID, PRODUCT_ID, CURRENCY, DATE ) );
    }

    @Test
    void validateGetPriceRequestWhenNullBrandIdShouldThrowApiBadRequestException()
    {
        var exception = assertThrows( ApiValidationException.class,
            () -> cut.validateGetPriceRequest( null, PRODUCT_ID, CURRENCY, DATE ) );

        assertNotNull( exception.getErrors() );
        assertEquals( 1, exception.getErrors().size() );
        assertEquals( "brandId", exception.getErrors().getFirst().getKey() );
    }

    @Test
    void validateGetPriceRequestWhenNegativeBrandIdShouldThrowApiBadRequestException()
    {
        var exception = assertThrows( ApiValidationException.class,
            () -> cut.validateGetPriceRequest( -1, PRODUCT_ID, CURRENCY, DATE ) );

        assertNotNull( exception.getErrors() );
        assertEquals( 1, exception.getErrors().size() );
        assertEquals( "brandId", exception.getErrors().getFirst().getKey() );
    }

    @Test
    void validateGetPriceRequestWhenNullProductIdShouldThrowApiBadRequestException()
    {
        var exception = assertThrows( ApiValidationException.class,
            () -> cut.validateGetPriceRequest( BRAND_ID, null, CURRENCY, DATE ) );

        assertNotNull( exception.getErrors() );
        assertEquals( 1, exception.getErrors().size() );
        assertEquals( "productId", exception.getErrors().getFirst().getKey() );
    }

    @Test
    void validateGetPriceRequestWhenNegativeProductIdShouldThrowApiBadRequestException()
    {
        var exception = assertThrows( ApiValidationException.class,
            () -> cut.validateGetPriceRequest( BRAND_ID, -1L, CURRENCY, DATE ) );

        assertNotNull( exception.getErrors() );
        assertEquals( 1, exception.getErrors().size() );
        assertEquals( "productId", exception.getErrors().getFirst().getKey() );
    }

    @Test
    void validateGetPriceRequestWithNullDateShouldThrowApiBadRequestException()
    {
        var exception = assertThrows( ApiValidationException.class,
            () -> cut.validateGetPriceRequest( BRAND_ID, PRODUCT_ID, CURRENCY, null ) );

        assertNotNull( exception.getErrors() );
        assertEquals( 1, exception.getErrors().size() );
        assertEquals( "date", exception.getErrors().getFirst().getKey() );
    }

    @Test
    void validateGetPriceRequestWhenNullCurrencyShouldThrowApiBadRequestException()
    {
        var exception = assertThrows( ApiValidationException.class,
            () -> cut.validateGetPriceRequest( BRAND_ID, PRODUCT_ID, null, DATE ) );

        assertNotNull( exception.getErrors() );
        assertEquals( 1, exception.getErrors().size() );
        assertEquals( "currency", exception.getErrors().getFirst().getKey() );
    }

    @Test
    void validateGetPriceRequestWhenEmptyCurrencyShouldThrowApiBadRequestException()
    {
        var exception = assertThrows( ApiValidationException.class,
            () -> cut.validateGetPriceRequest( BRAND_ID, PRODUCT_ID, "", DATE ) );

        assertNotNull( exception.getErrors() );
        assertEquals( 1, exception.getErrors().size() );
        assertEquals( "currency", exception.getErrors().getFirst().getKey() );
    }

    @Test
    void validateGetPriceRequestWithMultipleInvalidParamsShouldThrowApiBadRequestExceptionWithMultipleErrors()
    {
        var exception =
            assertThrows( ApiValidationException.class, () -> cut.validateGetPriceRequest( null, -1L, null, null ) );

        assertNotNull( exception.getErrors() );
        assertEquals( 4, exception.getErrors().size() );
        List<String> errorKeys = exception.getErrors().stream().map( ApiError::getKey ).toList();

        assertTrue( errorKeys.contains( "brandId" ) );
        assertTrue( errorKeys.contains( "productId" ) );
        assertTrue( errorKeys.contains( "currency" ) );
        assertTrue( errorKeys.contains( "date" ) );
    }
}