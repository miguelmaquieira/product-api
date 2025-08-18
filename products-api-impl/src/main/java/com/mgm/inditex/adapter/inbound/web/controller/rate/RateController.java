package com.mgm.inditex.adapter.inbound.web.controller.rate;

import java.time.OffsetDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.mgm.inditex.adapter.inbound.web.controller.rate.mapper.RateWebMapper;
import com.mgm.inditex.controller.api.PriceApiDelegate;
import com.mgm.inditex.controller.model.PriceResponse;
import com.mgm.inditex.core.usecase.RateUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling price-related requests.
 * This class acts as an **inbound adapter** in the hexagonal architecture,
 * exposing a REST API endpoint for clients to query for product prices. It
 * delegates the business logic to the core domain, adhering to the {@link PriceApiDelegate}
 * interface, which is typically generated from an OpenAPI specification.
 *
 *
 * @author Miguel Maquieira
 */
@Slf4j
@Tag( name = "Price", description = "API to fetch the applicable price for a product from the rates table." )
@RequiredArgsConstructor
@Component
public class RateController implements PriceApiDelegate
{
    private final RateUseCase rateUsecase;
    private final RateWebMapper rateWebMapper;

    /**
     * Retrieves the applicable price for a specific product, brand, date, and currency.
     * This method serves as the entry point for clients to get pricing information.
     * It uses the configured business logic to determine the most relevant price
     * based on priority and date ranges.
     *
     * @param brandId The unique identifier of the brand.
     * @param productId The unique identifier of the product.
     * @param date The date and time for which the price is valid.
     * @param currency The currency of the price.
     * @return A {@link ResponseEntity} containing a {@link PriceResponse} with the price details
     * if a valid price is found (HTTP 200 OK), or an HTTP 404 Not Found response if no price is applicable.
     */
    @Operation(
        summary = "Get the applicable price for a product at a given time.",
        description = "Returns the price for a product at a specific date and time based on priority and date ranges."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successful response, returns the price details.",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PriceResponse.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input. The date, productId, or brandId may be missing or in the wrong format."
    )
    @ApiResponse(
        responseCode = "404",
        description = "Price not found for the given product, brand, and date."
    )
    @Override
    public ResponseEntity<PriceResponse> getPrice( final Integer brandId, final Long productId,
        final OffsetDateTime date, final String currency )
    {
        var result = rateUsecase.getPrice( brandId, productId, currency, date.toLocalDateTime() );

        return result
            .map( rateWebMapper::domainToApi )
            .map( ResponseEntity::ok )
            .orElse( ResponseEntity.notFound().build() );
    }
}
