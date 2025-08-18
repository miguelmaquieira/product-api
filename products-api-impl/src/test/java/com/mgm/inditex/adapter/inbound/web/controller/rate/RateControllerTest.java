package com.mgm.inditex.adapter.inbound.web.controller.rate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mgm.inditex.adapter.inbound.web.controller.rate.mapper.RateWebMapper;
import com.mgm.inditex.controller.model.PriceResponse;
import com.mgm.inditex.core.domain.Rate;
import com.mgm.inditex.core.validation.ApiValidationException;
import com.mgm.inditex.core.usecase.RateUseCase;

// CSOFF: Javadoc
@ExtendWith( MockitoExtension.class)
class RateControllerTest
{
    private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 35455L;
    private static final String CURRENCY = "EUR";
    private static final Integer PRICE_LIST_ID = 1;
    private static final OffsetDateTime REQUEST_DATE_TIME = OffsetDateTime.of(2020, 6, 14, 10, 0, 0, 0, ZoneOffset.UTC);
    private static final LocalDateTime SERVICE_DATE_TIME = REQUEST_DATE_TIME.toLocalDateTime(); // Expected by service
    private static final Short PRIORITY = 1;

    @Mock
    private RateUseCase rateUsecase; // Mock the service port (RateService is the implementation)

    @Mock
    private RateWebMapper rateWebMapper;

    @InjectMocks
    private RateController rateController;

    @Test
    void getPriceWhenPriceFoundShouldReturnOkWithPriceResponse()
    {
        // Arrange
        var rate = buildRate();
        var priceResponse = buildPriceResponse();
        when( rateUsecase.getPrice(
            eq( BRAND_ID ),
            eq( PRODUCT_ID ),
            eq( CURRENCY ),
            eq( SERVICE_DATE_TIME ) ) ).thenReturn( Optional.of( rate ) );
        when( rateWebMapper.domainToApi( eq( rate ) ) ).thenReturn( priceResponse );

        // Act
        var response = rateController.getPrice( BRAND_ID, PRODUCT_ID, REQUEST_DATE_TIME, CURRENCY );

        // Assert
        assertNotNull( response );
        assertEquals( HttpStatus.OK, response.getStatusCode() );
        assertEquals( priceResponse, response.getBody() );

        // Verify interactions with mocks
        verify( rateUsecase, times( 1 ) ).getPrice(
            eq( BRAND_ID ),
            eq( PRODUCT_ID ),
            eq( CURRENCY ),
            eq( SERVICE_DATE_TIME ) );
        verify( rateWebMapper, times( 1 ) ).domainToApi( eq( rate ) );
    }

    @Test
    void getPrice_henPriceNotFoundShouldReturnNotFound()
    {
        // Arrange
        when( rateUsecase.getPrice(
            eq( BRAND_ID ),
            eq( PRODUCT_ID ),
            eq( CURRENCY ),
            eq( SERVICE_DATE_TIME ) ) ).thenReturn( Optional.empty() );

        // Act
        var response = rateController.getPrice( BRAND_ID, PRODUCT_ID, REQUEST_DATE_TIME, CURRENCY );

        // Assert
        assertNotNull( response );
        assertEquals( HttpStatus.NOT_FOUND, response.getStatusCode() );
        assertFalse( response.hasBody() ); // No body for 404 in this case

        // Verify interactions with mocks
        verify( rateUsecase, times( 1 ) ).getPrice(
            eq( BRAND_ID ),
            eq( PRODUCT_ID ),
            eq( CURRENCY ),
            eq( SERVICE_DATE_TIME ) );
        verify( rateWebMapper, never() ).domainToApi( any( Rate.class ) ); // Mapper should not be called
    }

    @Test
    void getPriceWhenServiceThrowsBadRequestExceptionShouldPropagateException()
    {
        // Arrange
        // Simulate a validation error from the service layer
        Mockito.doThrow( new ApiValidationException( Collections.emptyList() ) )
            .when( rateUsecase )
            .getPrice( eq( BRAND_ID ), eq( PRODUCT_ID ), eq( CURRENCY ), eq( SERVICE_DATE_TIME ) );

        // Act & Assert
        // Expect the ApiBadRequestException to be thrown from the controller method
        assertThrows( ApiValidationException.class,
            () -> rateController.getPrice( BRAND_ID, PRODUCT_ID, REQUEST_DATE_TIME, CURRENCY ) );

        // Verify interactions with mocks
        verify( rateUsecase, times( 1 ) ).getPrice(
            eq( BRAND_ID ),
            eq( PRODUCT_ID ),
            eq( CURRENCY ),
            eq( SERVICE_DATE_TIME ) );
        verify( rateWebMapper, never() ).domainToApi( any( Rate.class ) );
    }

    private Rate buildRate()
    {
        return Rate.of(
            BRAND_ID,
            PRODUCT_ID,
            PRICE_LIST_ID,
            SERVICE_DATE_TIME.minusDays( 5 ),
            SERVICE_DATE_TIME.plusDays( 5 ),
            PRIORITY,
            new BigDecimal( "10.0500" ),
            CURRENCY
        );
    }

    private PriceResponse buildPriceResponse()
    {
        var priceResponse = new PriceResponse();
        priceResponse.setBrandId( BRAND_ID );
        priceResponse.setProductId( PRODUCT_ID );
        priceResponse.setCurrency( CURRENCY );
        priceResponse.setPriceList( 1 );
        priceResponse.setStartDate( REQUEST_DATE_TIME.minusDays( 5 ) );
        priceResponse.setEndDate( REQUEST_DATE_TIME.plusDays( 5 ) );
        return priceResponse;
    }
}