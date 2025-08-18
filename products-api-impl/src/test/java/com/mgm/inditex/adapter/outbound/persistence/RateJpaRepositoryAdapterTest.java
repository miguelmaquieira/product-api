package com.mgm.inditex.adapter.outbound.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.mgm.inditex.adapter.outbound.persistence.jpa.RateJpaRepository;
import com.mgm.inditex.adapter.outbound.persistence.jpa.entity.RateJpaEntity;
import com.mgm.inditex.adapter.outbound.persistence.jpa.mapper.RateJpaMapper;
import com.mgm.inditex.core.domain.Rate;

// CSOFF: Javadoc
@ExtendWith( MockitoExtension.class )
class RateJpaRepositoryAdapterTest
{
    private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 35455L;
    private static final String CURRENCY = "EUR";
    private static final Integer PRICE_LIST_ID = 1;
    private static final LocalDateTime SERVICE_DATE_TIME = LocalDateTime.of( 2020, 6, 14, 10, 0 );
    private static final Instant DATE = SERVICE_DATE_TIME.toInstant( ZoneOffset.UTC );

    @Mock
    private RateJpaRepository jpaRepository;

    @Mock
    private RateJpaMapper mapper;

    @InjectMocks
    private RateJpaRepositoryAdapter cut;

    private Rate rate1;
    private Rate rate2;

    @BeforeEach
    void setUp()
    {
        short priority1 = 1;
        short priority2 = 2;
        rate1 = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, SERVICE_DATE_TIME.minusDays( 5 ),
            SERVICE_DATE_TIME.plusDays( 5 ), priority1, new BigDecimal( "10.05" ), CURRENCY );

        rate2 = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, SERVICE_DATE_TIME.minusDays( 5 ),
            SERVICE_DATE_TIME.plusDays( 5 ), priority2, new BigDecimal( "11.05" ), CURRENCY );
    }

    @Test
    void findRatesForBrandAndProductAndCurrencyWhenResultsShouldReturnMappedRates()
    {
        // given
        var e1 = mock( RateJpaEntity.class );
        var e2 = mock( RateJpaEntity.class );

        // when
        when( jpaRepository.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURRENCY, DATE ) ).thenReturn(
            List.of( e1, e2 ) );
        when( mapper.rateJpaEntityToRate( e1 ) ).thenReturn( rate1 );
        when( mapper.rateJpaEntityToRate( e2 ) ).thenReturn( rate2 );

        var result = cut.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURRENCY, DATE );

        // then
        assertNotNull( result );
        assertEquals( 2, result.size() );
        assertEquals( List.of( rate1, rate2 ), result );

        // verify
        verify( jpaRepository, times( 1 ) ).findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURRENCY,
            DATE );
        verify( mapper, times( 2 ) ).rateJpaEntityToRate( any( RateJpaEntity.class ) );
        verifyNoMoreInteractions( jpaRepository, mapper );
    }

    @Test
    void findRatesForBrandAndProductAndCurrencyWhenEmptyShouldReturnsEmptyList()
    {
        // given
        when( jpaRepository.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURRENCY, DATE ) ).thenReturn(
            List.of() );

        // when
        var result = cut.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURRENCY, DATE );

        // then
        assertNotNull( result );
        assertTrue( result.isEmpty() );
        verify( jpaRepository, times( 1 ) ).findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURRENCY,
            DATE );
        verifyNoInteractions( mapper );
    }

    @Test
    void findRatesForBrandAndProductWhenResultsShouldReturnMappedRates()
    {
        // given
        var e1 = mock( RateJpaEntity.class );

        when( jpaRepository.findRatesForBrandAndProduct( BRAND_ID, PRODUCT_ID, DATE ) ).thenReturn( List.of( e1 ) );
        when( mapper.rateJpaEntityToRate( e1 ) ).thenReturn( rate1 );

        // when
        var result = cut.findRatesForBrandAndProduct( BRAND_ID, PRODUCT_ID, DATE );

        // then
        assertNotNull( result );
        assertEquals( 1, result.size() );
        assertEquals( rate1, result.getFirst() );

        // verify repo call + args captured
        ArgumentCaptor<Integer> capBrand = ArgumentCaptor.forClass( Integer.class );
        ArgumentCaptor<Long> capProduct = ArgumentCaptor.forClass( Long.class );
        ArgumentCaptor<Instant> capAt = ArgumentCaptor.forClass( Instant.class );

        verify( jpaRepository ).findRatesForBrandAndProduct( capBrand.capture(), capProduct.capture(),
            capAt.capture() );
        assertEquals( BRAND_ID, capBrand.getValue() );
        assertEquals( PRODUCT_ID, capProduct.getValue() );
        assertEquals( DATE, capAt.getValue() );

        verify( mapper ).rateJpaEntityToRate( e1 );
        verifyNoMoreInteractions( mapper, jpaRepository );
    }

    @Test
    void findRatesForBrandAndProductWhenEmptyShouldReturnEmptyList()
    {
        // given
        when( jpaRepository.findRatesForBrandAndProduct( BRAND_ID, PRODUCT_ID, DATE ) ).thenReturn( List.of() );

        // when
        var result = cut.findRatesForBrandAndProduct( BRAND_ID, PRODUCT_ID, DATE );

        // then
        assertNotNull( result );
        assertTrue( result.isEmpty() );
        verify( jpaRepository ).findRatesForBrandAndProduct( BRAND_ID, PRODUCT_ID, DATE );

        verifyNoInteractions( mapper );
    }
}