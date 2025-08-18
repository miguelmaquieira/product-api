package com.mgm.inditex.integration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgm.inditex.controller.model.PriceResponse;

// CSOFF
@SpringBootTest
@AutoConfigureMockMvc( addFilters = false )
@Sql( scripts = {"/sql/clear-tables.sql", "/sql/integration-test-data.sql"} )
public class GetPriceIntegrationTest
{
    private static final String GET_PRICE_PATH = "/inditex/api/v1/prices";
    private static final String BRAND_ID_PARAM_KEY = "brandId";
    private static final String PRODUCT_ID_PARAM_KEY = "productId";
    private static final String DATE_PARAM_KEY = "date";
    private static final String CURRENCY_PARAM_KEY = "currency";
    private static final String CURRENCY = "EUR";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @ParameterizedTest
    @MethodSource( "providedBrandProductDateAndExpectedResults" )
    void getPrices( final Integer brandId, final Long productId, final OffsetDateTime date, final String currency,
        final BigDecimal price ) throws Exception
    {
        var response = mockMvc.perform( get( GET_PRICE_PATH )
            .param( BRAND_ID_PARAM_KEY, brandId.toString() )
            .param( PRODUCT_ID_PARAM_KEY, productId.toString() )
            .param( DATE_PARAM_KEY, date.toString() )
            .param( CURRENCY_PARAM_KEY, currency )
            .contentType( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() ).andReturn();

        var resultJson = response.getResponse().getContentAsString();
        var result = mapper.readValue( resultJson, PriceResponse.class );

        assertNotNull( result );
        assertEquals( price, result.getPrice() );
    }

    static Stream<Arguments> providedBrandProductDateAndExpectedResults()
    {
        return Stream.of(
            Arguments.of(
                1,
                35455L,
                LocalDateTime.of( 2020, 6, 14, 10, 0 ).atOffset( ZoneOffset.UTC ),
                CURRENCY,
                new BigDecimal( "35.50" ) ),
            Arguments.of(
                1,
                35455L,
                LocalDateTime.of( 2020, 6, 14, 16, 0 ).atOffset( ZoneOffset.UTC ),
                CURRENCY,
                new BigDecimal( "25.45" ) ),
            Arguments.of(
                1,
                35455L,
                LocalDateTime.of( 2020, 6, 14, 21, 0 ).atOffset( ZoneOffset.UTC ),
                CURRENCY,
                new BigDecimal( "35.50" ) ),
            Arguments.of(
                1,
                35455L, LocalDateTime.of( 2020, 6, 15, 16, 0 ).atOffset( ZoneOffset.UTC ),
                CURRENCY,
                new BigDecimal(  "30.50" ) ),
            Arguments.of(
                1,
                35455L,
                LocalDateTime.of( 2020, 6, 15, 21, 0 ).atOffset( ZoneOffset.UTC ),
                CURRENCY,
                new BigDecimal( "38.95" ) )
        );
    }
}
