package com.mgm.inditex.core.validation;

import java.time.LocalDateTime;

/**
 * Defines the contract for validating inbound requests to the core domain services.
 *
 * @author Miguel Maquieira
 */
public interface RateValidator
{
    /**
     * Validates the parameters for a request to retrieve a product price.
     * <p>
     * This method ensures that all necessary parameters are present and conform
     * to the business rules before the price lookup is performed.
     * </p>
     * @param brandId The unique identifier of the brand.
     * @param productId The unique identifier of the product.
     * @param currency The currency of the price.
     * @param date The date and time for which the price is valid.
     * @throws ApiValidationException if any of the parameters are invalid.
     */
    void validateGetPriceRequest( Integer brandId, Long productId, String currency, LocalDateTime date );
}
