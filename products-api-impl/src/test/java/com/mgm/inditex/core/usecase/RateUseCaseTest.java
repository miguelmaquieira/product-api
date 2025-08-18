package com.mgm.inditex.core.usecase;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mgm.inditex.core.domain.Rate;
import com.mgm.inditex.core.port.outbound.RateRepositoryPort;
import com.mgm.inditex.core.validation.ApiValidationException;
import com.mgm.inditex.core.validation.RateValidator;

// CSOFF: Javadoc
@ExtendWith( MockitoExtension.class )
class RateUseCaseTest
{
    private static final long PRODUCT_ID = 35455L;
    private static final int BRAND_ID = 1;
    private static final int PRICE_LIST_ID = 1;
    private static final String CURR = "EUR";
    private static final Short PRIORITY = 1;
    private static final LocalDateTime DATE = LocalDateTime.of( 2020, 6, 14, 0, 0 );
    private static final Instant DATE_TO_INSTANT = DATE.toInstant( ZoneOffset.UTC );

    @Mock
    private RateRepositoryPort rateRepository;

    @Mock
    private RateValidator rateValidator;

    @InjectMocks
    private RateUseCase rateUsecase;

    @Test
    void getPriceWhenRatesFoundShouldReturnHighestPriorityRate()
    {
        // Arrange
        short priority1 = 1;
        short priority2 = 2;
        var rate1 = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, DATE.minusDays( 5 ), DATE.plusDays( 5 ), priority1,
            new BigDecimal( "25.5000" ), CURR );
        var rate2 = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID,  DATE.minusDays( 5 ), DATE, priority2,
            new BigDecimal( "35.5000" ), CURR );

        var rates = List.of( rate1, rate2 );

        when( rateRepository.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR, DATE_TO_INSTANT ) )
            .thenReturn( rates );

        // Act
        var result = rateUsecase.getPrice( BRAND_ID, PRODUCT_ID, CURR, DATE );

        // Assert
        assertTrue( result.isPresent() );
        assertEquals( rate2, result.get() );
        verify( rateRepository, times( 1 ) ).findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR,
            DATE_TO_INSTANT );
    }


    @Test
    void getPriceWhenNoRatesFoundShouldReturnEmptyOptional() {
        // Arrange
        when( rateRepository.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR, DATE_TO_INSTANT ) )
            .thenReturn( Collections.emptyList() );

        // Act
        var result = rateUsecase.getPrice( BRAND_ID, PRODUCT_ID, CURR, DATE );

        // Assert
        assertFalse( result.isPresent() );
        verify( rateRepository, times( 1 ) )
            .findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR, DATE_TO_INSTANT );
    }

    @Test
    void getPriceWhenMultipleRatesWithSamePriorityShouldReturnFirstOneFound()
    {
        // Arrange
        var rate1 = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, DATE.minusDays( 5 ), DATE.plusDays( 5 ), PRIORITY,
            new BigDecimal( "25.5000" ), CURR );
        var rate2 = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID,  DATE.minusDays( 5 ), DATE.plusDays( 1 ), PRIORITY,
            new BigDecimal( "35.5000" ), CURR );

        var rates = List.of( rate1, rate2 );

        when( rateRepository.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR, DATE_TO_INSTANT ) )
            .thenReturn( rates );

        // Act
        var result = rateUsecase.getPrice( BRAND_ID, PRODUCT_ID, CURR, DATE );

        // Assert
        assertTrue( result.isPresent() );
        assertEquals( rate1, result.get() );
        verify( rateRepository, times( 1 ) )
            .findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR, DATE_TO_INSTANT );
    }

    @Test
    void getPriceWhenBrandIdNullShouldThrowException()
    {
        // Arrange
        doThrow( new ApiValidationException( Collections.emptyList() ) )
            .when( rateValidator )
            .validateGetPriceRequest( any(), anyLong(), anyString(), any( LocalDateTime.class ) );

        // Act & Assert
        assertThrows( ApiValidationException.class, () -> rateUsecase.getPrice( null, PRODUCT_ID, CURR, DATE ) );

        verify( rateRepository, never() )
            .findRatesForBrandAndProductAndCurrency( any(), anyLong(), anyString(), any( Instant.class ) );
    }
}