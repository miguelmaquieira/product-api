package com.mgm.inditex.core.port.outbound;

import java.time.Instant;
import java.util.List;

import com.mgm.inditex.core.domain.Rate;

/**
 * Interface to define repository operations for managing rate data for products.
 *
 * @author Miguel Maquieira
 */
public interface RateRepositoryPort
{
    /**
     * Find the list of rates for a given product, price list, and currency at a specified date.
     *
     * @param brandId The brand ID.
     * @param productId The product ID.
     * @param currency The currency in which the rate is expressed.
     * @param date The date for which the rate is valid.
     * @return A list of rates for the product, price and currency list on the given date
     */
    List<Rate> findRatesForBrandAndProductAndCurrency( Integer brandId, Long productId, String currency,
        Instant date );

    /**
     * Find all rates for a given product and price list at a specified date.
     *
     * @param brandId The brand ID.
     * @param productId The product ID.
     * @param date The date for which the rates are valid.
     * @return A list of rates for the product and price list on the given date.
     */
    List<Rate> findRatesForBrandAndProduct( Integer brandId, Long productId, Instant date );
}
