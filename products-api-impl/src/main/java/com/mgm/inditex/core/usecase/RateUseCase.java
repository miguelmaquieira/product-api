package com.mgm.inditex.core.usecase;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.kv;

import org.springframework.stereotype.Service;

import com.mgm.inditex.core.domain.Rate;
import com.mgm.inditex.core.port.inbound.RateUserCasePort;
import com.mgm.inditex.core.port.outbound.RateRepositoryPort;
import com.mgm.inditex.core.validation.RateValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class that handles the business logic for fetching the applicable price for a product.
 * This service interacts with the necessary data sources (e.g., databases) to retrieve the price
 * based on the given product ID, brand ID, and date.
 * <p>
 * The price retrieval process considers the brand, product, and the date to determine the most
 * relevant price based on certain business rules such as priority and date range.
 *
 *
 * @author Miguel Maquieira
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateUseCase implements RateUserCasePort
{
    private final RateRepositoryPort rateRepository;
    private final RateValidator rateValidator;

    @Override
    public Optional<Rate> getPrice( final Integer brandId, final Long productId, final String currency,
        final LocalDateTime date )
    {
        rateValidator.validateGetPriceRequest( brandId, productId, currency, date );

        log.debug( "Getting price for product. {] {} {} {}",
            kv( "brandId", brandId ),
            kv( "productId", productId ),
            kv( "date", date ),
            kv( "currency", currency ) );

        var rates = rateRepository.findRatesForBrandAndProductAndCurrency( brandId, productId, currency,
            date.toInstant( ZoneOffset.UTC) );

        return rates.stream()
            .max( Comparator.comparingInt( Rate::getPriority ) )
            .map( r ->
            {
                log.info( "Price for query. {} {} {} {} {}",
                    kv( "brandId", brandId ),
                    kv( "productId", productId ),
                    kv( "date", date ),
                    kv( "currency", currency ),
                    kv( "price", r.getPrice() ) );
                return r;
            } )
            .or( () ->
            {
                log.warn( "No price found for product. {} {} {} {}",
                    kv( "brandId", brandId ),
                    kv( "productId", productId ),
                    kv( "date", date ),
                    kv( "currency", currency ) );
                return Optional.empty();
            } );
    }
}
