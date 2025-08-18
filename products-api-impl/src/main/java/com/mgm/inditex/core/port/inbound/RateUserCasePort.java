package com.mgm.inditex.core.port.inbound;

import java.time.LocalDateTime;
import java.util.Optional;

import com.mgm.inditex.adapter.outbound.persistence.jpa.entity.RateJpaEntity;
import com.mgm.inditex.core.domain.Rate;

/**
 * Port interface that defines the operations for managing and retrieving product prices.
 * This interface abstracts the service logic for fetching the applicable price based on
 * product, brand, date, and currency.
 * <p>
 * The implementation of this interface is expected to provide the actual business logic
 * to retrieve the price, considering various factors such as priority, date ranges, and currency.
 *
 *
 * @author Miguel Maquieira
 */
public interface RateUserCasePort
{

    /**
     * Retrieves the applicable price for a given product, brand, date, and currency.
     * The method uses the product ID, brand ID, date, and currency to calculate and return
     * the correct price from the available price records.
     *
     * <p>This method may return a {@link RateJpaEntity} if a valid price is found or {@code null}
     * if no price matches the criteria.</p>
     *
     * @param brandId The unique identifier of the brand to which the product belongs.
     * @param productId The unique identifier of the product.
     * @param currency The currency in which the price should be returned (e.g., "EUR", "USD").
     * @param date The date and time at which the price should be applicable, in ISO 8601 format.
     * @return The applicable {@link Rate} for the given parameters, or {@code null} if no applicable price is found.
     */
    Optional<Rate> getPrice( Integer brandId, Long productId, String currency, LocalDateTime date );
}
