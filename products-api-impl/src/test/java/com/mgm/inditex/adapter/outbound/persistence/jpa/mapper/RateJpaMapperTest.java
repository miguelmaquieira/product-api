package com.mgm.inditex.adapter.outbound.persistence.jpa.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;

import com.mgm.inditex.adapter.outbound.persistence.jpa.entity.RateJpaEntity;

// CSOFF: Javadoc
class RateJpaMapperTest
{
    private static final long PRODUCT_ID = 35455L;
    private static final int BRAND_ID = 1;
    private static final int PRICE_LIST = 1;
    private static final String CURR = "EUR";
    private static final Instant START_DATE = Instant.parse( "2020-06-14T00:00:00Z" );
    private static final Instant END_DATE = Instant.parse( "2020-12-14T00:00:00Z" );
    private static final Short PRIORITY = 1;

    private final RateJpaMapper cut = Mappers.getMapper( RateJpaMapper.class );

    @Test
    void rateJpaEntityToRate()
    {
        // Given: a Rate entity
        RateJpaEntity rateJpaEntity = new RateJpaEntity();
        rateJpaEntity.setProductId( PRODUCT_ID );
        rateJpaEntity.setBrandId( BRAND_ID );
        rateJpaEntity.setPriceListId( PRICE_LIST );
        rateJpaEntity.setPrice( new BigDecimal( "35.5000" ) );
        rateJpaEntity.setPriority( PRIORITY );
        rateJpaEntity.setStartDate( START_DATE );
        rateJpaEntity.setEndDate( END_DATE );
        rateJpaEntity.setCurrency( CURR );

        // When: converting Rate to RateDto
        var rateDto = cut.rateJpaEntityToRate( rateJpaEntity );

        // Then: the RateDto should have the same values as the Rate entity
        assertNotNull( rateDto );
        assertEquals( rateJpaEntity.getProductId(), rateDto.getProductId() );
        assertEquals( rateJpaEntity.getBrandId(), rateDto.getBrandId() );
        assertEquals( rateJpaEntity.getPriceListId(), rateDto.getPriceListId() );
        assertEquals( new BigDecimal( "35.5000" ), rateDto.getPrice() );
        assertEquals( LocalDateTime.ofInstant( rateJpaEntity.getStartDate(), ZoneOffset.UTC ), rateDto.getStartDate() );
        assertEquals( LocalDateTime.ofInstant( rateJpaEntity.getEndDate(), ZoneOffset.UTC ), rateDto.getEndDate() );
        assertEquals( rateJpaEntity.getCurrency(), rateDto.getCurrency() );
    }
}