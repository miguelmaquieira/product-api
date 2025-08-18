package com.mgm.inditex.adapter.inbound.web.controller.rate.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;

import com.mgm.inditex.core.domain.Rate;

// CSOFF: Javadoc
class RateWebMapperTest
{
    private static final long PRODUCT_ID = 35455L;
    private static final int BRAND_ID = 1;
    private static final int PRICE_LIST_ID = 1;
    private static final String CURR = "EUR";
    private static final LocalDateTime START_DATE = LocalDateTime.of( 2020, 6, 14, 0, 0 );
    private static final LocalDateTime END_DATE = LocalDateTime.of( 2020, 12, 14, 0, 0 );
    private static final Short PRIORITY = 1;

    private final RateWebMapper cut = Mappers.getMapper( RateWebMapper.class );

    @Test
    void domainToApi()
    {
        // Given: a Rate DTO
        var rate = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY,
            new BigDecimal( "35.5000" ), CURR );

        // When: converting RateDto to Rate
        var rateResponse = cut.domainToApi( rate );

        // Then: the Rate should have the same values as the RateDto entity
        assertNotNull( rateResponse );
        assertEquals( rate.getProductId(), rateResponse.getProductId() );
        assertEquals( rate.getBrandId(), rateResponse.getBrandId() );
        assertEquals( rate.getPriceListId(), rateResponse.getPriceList() );
        assertEquals( rate.getCurrency(), rateResponse.getCurrency() );
        assertEquals( new BigDecimal( "35.50" ), rateResponse.getPrice() );
        assertEquals( rate.getStartDate().atOffset( ZoneOffset.UTC ), rateResponse.getStartDate() );
        assertEquals( rate.getEndDate().atOffset( ZoneOffset.UTC ), rateResponse.getEndDate() );
    }
}