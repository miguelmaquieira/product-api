package com.mgm.inditex.adapter.outbound.persistence.jpa;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mgm.inditex.adapter.outbound.persistence.jpa.entity.RateJpaEntity;

// CSOFF: Javadoc
@DataJpaTest
@TestMethodOrder( MethodOrderer.MethodName.class )
class RateJpaRepositoryTest
{

    private static final long PRODUCT_ID = 35455L;
    private static final int BRAND_ID = 1;
    private static final String CURR = "EUR";
    private static final Instant START_DATE_DEFAULT = Instant.parse( "2025-01-01T12:00:00Z" );
    private static final Instant END_DATE_DEFAULT = Instant.parse( "2025-12-01T12:00:00Z" );

    @Autowired
    private RateJpaRepository cut;

    @Test
    @Sql( scripts = { "/sql/test-data.sql" } )
    void shouldLoadPricesFromSqlScript()
    {
        var result = cut.findAll();

        assertEquals( 6, result.size(), "Should load 4 prices" );
    }

    @Test
    void findByIdWhenPriceExistShouldSaveAndLoadProduct()
    {
        // Given
        var productListId = 1;
        short priority = 1;
        var priceValue = 35.0;

        var rate = new RateJpaEntity();
        rate.setProductId( PRODUCT_ID );
        rate.setPriceListId( productListId );
        rate.setCurrency( CURR );
        rate.setPriority( priority );
        rate.setBrandId( BRAND_ID );
        rate.setEndDate( END_DATE_DEFAULT );
        rate.setStartDate( START_DATE_DEFAULT );
        rate.setPrice( new BigDecimal( priceValue ) );

        // When
        var savedRate = cut.save( rate );

        var found = cut.findById( savedRate.getId() );

        // Assert
        assertNotNull( found );
        assertTrue( found.isPresent() );
        var item = found.get();
        assertEquals( CURR, item.getCurrency() );
        assertEquals( priority, item.getPriority() );
        assertEquals( PRODUCT_ID, item.getProductId() );
    }

    @Test
    void findAllWhenTwoPricesShouldSaveAndLoadRates()
    {
        // Given
        var productListId = 1;
        var curr1 = "EUR";
        var curr2 = "USD";
        short priority = 1;

        var rate1 = new RateJpaEntity();
        rate1.setProductId( PRODUCT_ID );
        rate1.setPriceListId( productListId );
        rate1.setCurrency( curr1 );
        rate1.setPriority( priority );
        rate1.setBrandId( BRAND_ID );
        rate1.setEndDate( END_DATE_DEFAULT );
        rate1.setStartDate( START_DATE_DEFAULT );
        rate1.setPrice( new BigDecimal( "25.0" ) );

        var rate2 = new RateJpaEntity();
        rate2.setProductId( PRODUCT_ID );
        rate2.setPriceListId( productListId );
        rate2.setCurrency( curr2 );
        rate2.setPriority( priority );
        rate2.setBrandId( BRAND_ID );
        rate2.setEndDate( END_DATE_DEFAULT );
        rate2.setStartDate( START_DATE_DEFAULT );
        rate2.setPrice( new BigDecimal( "26.0" ));

        // When
        cut.save( rate1 );
        cut.save( rate2 );

        var result = cut.findAll();

        // Assert
        assertNotNull( result );
        assertEquals( 2, result.size() );
    }

    @Test
    void saveWhenTwoRateHaveTheSameCurrencyShouldThrowException()
    {
        // Given
        var productListId = 1;
        short priority = 1;

        var rate1 = new RateJpaEntity();
        rate1.setProductId( PRODUCT_ID );
        rate1.setPriceListId( productListId );
        rate1.setCurrency( CURR );
        rate1.setPriority( priority );
        rate1.setBrandId( BRAND_ID );
        rate1.setEndDate( END_DATE_DEFAULT );
        rate1.setStartDate( START_DATE_DEFAULT );
        rate1.setPrice( new BigDecimal( "25.0" ) );

        var rate2 = new RateJpaEntity();
        rate2.setProductId( PRODUCT_ID );
        rate2.setPriceListId( productListId );
        rate2.setCurrency( CURR );
        rate2.setPriority( priority );
        rate2.setBrandId( BRAND_ID );
        rate2.setEndDate( END_DATE_DEFAULT );
        rate2.setStartDate( START_DATE_DEFAULT );

        rate2.setPrice( new BigDecimal( "26.0" ) );

        // Assert
        cut.save( rate1 );
        assertThrows( DataIntegrityViolationException.class, () -> cut.save( rate2 ) );
    }

    @Test
    @Sql( scripts = { "/sql/test-data.sql" } )
    void findRateForProductAndBrandAndCurrencyWhenItemExistShouldReturnRateAndProduct()
    {
        var DATE = Instant.parse( "2020-06-14T13:00:00Z" );
        var result = cut.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR, DATE );

        assertNotNull( result );
        assertFalse( result.isEmpty() );
        assertEquals( 1, result.size() );

        DATE = Instant.parse( "2020-06-14T16:00:00Z" );
        result = cut.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR, DATE );

        assertNotNull( result );
        assertFalse( result.isEmpty() );
        assertEquals( 2, result.size() );

        DATE = Instant.parse( "2025-06-14T16:00:00Z" );
        result = cut.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR, DATE );
        assertNotNull( result );
        assertTrue( result.isEmpty() );

        DATE = Instant.parse( "2020-06-15T19:00:00Z" );
        result = cut.findRatesForBrandAndProductAndCurrency( BRAND_ID, PRODUCT_ID, CURR, DATE );

        assertNotNull( result );
        assertFalse( result.isEmpty() );
        assertEquals( 4, result.size() );
    }

    @Test
    @Sql( scripts = { "/sql/test-data.sql" } )
    void findRateForProductAndBrandWhenItemExistShouldReturnRateAndProduct()
    {
        var DATE = LocalDateTime.of( 2020, Month.JUNE, 14, 13, 0 ).toInstant( ZoneOffset.UTC );
        var result = cut.findRatesForBrandAndProduct( BRAND_ID, PRODUCT_ID, DATE );
        var expectedPrice = new BigDecimal( "35.5000" );

        assertNotNull( result );
        assertFalse( result.isEmpty() );
        assertEquals( 1, result.size() );
        assertEquals( expectedPrice, result.getFirst().getPrice() );

        DATE = LocalDateTime.of( 2020, Month.JUNE, 14, 16, 0 ).toInstant( ZoneOffset.UTC );
        result = cut.findRatesForBrandAndProduct( BRAND_ID, PRODUCT_ID, DATE );

        assertNotNull( result );
        assertFalse( result.isEmpty() );
        assertEquals( 3, result.size() );
    }
}