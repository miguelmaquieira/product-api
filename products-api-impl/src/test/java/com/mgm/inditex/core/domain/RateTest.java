package com.mgm.inditex.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// CSOFF: Javadoc
class RateTest
{
    private static final Integer BRAND_ID               = 1;
    private static final Long PRODUCT_ID                = 35455L;
    private static final Integer PRICE_LIST_ID          = 2;
    private static final LocalDateTime START_DATE       = LocalDateTime.of( 2020, 6, 14, 0, 0 );
    private static final LocalDateTime END_DATE         = LocalDateTime.of( 2020, 6, 15, 23, 59 );
    private static final Short PRIORITY                 = 1;
    private static final BigDecimal PRICE               = new BigDecimal( "35.5" );
    private static final String CCY                     = "EUR";

    @Test
    void rateOfWhenHappyPath()
    {
        var rate = createRate();

        assertNotNull( rate );
        assertEquals( BRAND_ID, rate.getBrandId() );
        assertEquals( PRODUCT_ID, rate.getProductId() );
        assertEquals( PRICE_LIST_ID, rate.getPriceListId() );
        assertEquals( START_DATE, rate.getStartDate() );
        assertEquals( END_DATE, rate.getEndDate() );
        assertEquals( PRIORITY, rate.getPriority() );
        assertEquals( new BigDecimal( "35.5000" ), rate.getPrice() );
        assertEquals( CCY, rate.getCurrency() );
    }

    @Test
    void rateOfWhenPriceIsNegativeShouldThrowException()
    {
        var exception = assertThrows( IllegalArgumentException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY,
                new BigDecimal( "-0.0001" ), CCY ) );

        assertTrue( exception.getMessage().toLowerCase().contains( "price" ) );
    }

    @Test
    void rateOfWhenPriceIsNegativeShouldThrowExceptionAskChatGPT()
    {
        var rate = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY,
                new BigDecimal( "-0.00001" ), CCY );

        assertNotNull(  rate );
        assertEquals( new BigDecimal( "0.0000" ), rate.getPrice() );
    }

    @Test
    void rateOfWhenCurrencyIsNotISOShouldThrowException()
    {
        assertThrows( IllegalArgumentException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY, PRICE, "EU" ) );
        assertThrows( IllegalArgumentException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY, PRICE, "EURO" ) );
        assertThrows( NullPointerException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY, PRICE, null ) );
        assertThrows( IllegalArgumentException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY, PRICE, "" ) );
    }

    @Test
    void rateOfWhenStartDateIsNotStrictlyBeforeEndDateShouldThrowException()
    {
        // equal
        assertThrows( IllegalArgumentException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, START_DATE, PRIORITY, PRICE, CCY ) );

        // start date after end date
        assertThrows( IllegalArgumentException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, END_DATE, START_DATE, PRIORITY, PRICE, CCY ) );
    }

    @Test
    void rateOfWhenRequiredFieldsAreNotSetShouldThrowException()
    {
        assertThrows( NullPointerException.class,
            () -> Rate.of( null, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY, PRICE, CCY ) );
        assertThrows( NullPointerException.class,
            () -> Rate.of( BRAND_ID, null, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY, PRICE, CCY ) );
        assertThrows( NullPointerException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, null, START_DATE, END_DATE, PRIORITY, PRICE, CCY ) );
        assertThrows( NullPointerException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, null, END_DATE, PRIORITY, PRICE, CCY ) );
        assertThrows( NullPointerException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, null, PRIORITY, PRICE, CCY ) );
        assertThrows( NullPointerException.class,
            () -> Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, null, PRICE, CCY ) );
    }

    @ParameterizedTest
    @MethodSource( "validInstants" )
    void appliesAtWhenValidInstants( final LocalDateTime at )
    {
        var rate = createRate();
        assertTrue( rate.appliesAt( at ) );
    }

    static Stream<LocalDateTime> validInstants()
    {
        return Stream.of(
            START_DATE,
            START_DATE.plusMinutes( 1 ),
            END_DATE.minusMinutes( 1 ),
            END_DATE
        );
    }

    @ParameterizedTest
    @MethodSource( "invalidInstants" )
    void appliesAtWhenInvalidInstants( final LocalDateTime at )
    {
        var rate = createRate();
        assertFalse( rate.appliesAt( at ) );
    }

    static Stream<LocalDateTime> invalidInstants()
    {
        return Stream.of(
            START_DATE.minusNanos( 1 ),
            END_DATE.plusNanos( 1 )
        );
    }

    @Test
    void equalAndHashCode()
    {
        var rate1 = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY, PRICE, CCY );
        var rate2 = Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY,
            new BigDecimal( "35.5000" ), CCY );
        var rate3 = Rate.of( 2, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY, PRICE, CCY );

        assertEquals( rate1, rate2 );
        assertEquals( rate1.hashCode(), rate2.hashCode() );
        assertNotEquals( rate1, rate3 );
    }

    @Test
    void toStringWhenContainsFields()
    {
        var rateToString = createRate().toString();
        assertNotNull( rateToString );
        assertFalse( rateToString.isBlank() );
        assertTrue( rateToString.contains( "brandId=" + BRAND_ID ) );
        assertTrue( rateToString.contains( "productId=" + PRODUCT_ID ) );
        assertTrue( rateToString.contains( "priceListId=" + PRICE_LIST_ID ) );
    }

    private static Rate createRate()
    {
        return Rate.of( BRAND_ID, PRODUCT_ID, PRICE_LIST_ID, START_DATE, END_DATE, PRIORITY, PRICE, CCY );
    }

}