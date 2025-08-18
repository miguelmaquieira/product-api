package com.mgm.inditex.adapter.outbound.persistence;

import java.time.Instant;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.mgm.inditex.adapter.outbound.persistence.jpa.RateJpaRepository;
import com.mgm.inditex.adapter.outbound.persistence.jpa.mapper.RateJpaMapper;
import com.mgm.inditex.core.domain.Rate;
import com.mgm.inditex.core.port.outbound.RateRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adapter that implements the {@link RateRepositoryPort} using JPA.
 * <p>
 * Delegates persistence operations to {@link RateJpaRepository}
 * and maps entities to the domain model {@link Rate}.
 * </p>
 *
 * @author Miguel Maquieira
 */
@Slf4j
@RequiredArgsConstructor
public class RateJpaRepositoryAdapter implements RateRepositoryPort
{
    private final RateJpaRepository jpaRepository;
    private final RateJpaMapper mapper;

    @Override
    public List<Rate> findRatesForBrandAndProductAndCurrency( final Integer brandId, final Long productId,
        final String currency, final Instant date )
    {
        log.debug( "Finding rates. {} {} {} {}",
            kv( "brandId", brandId ),
            kv( "productId", productId ),
            kv( "currency", currency ),
            kv( "date", date ) );

        var entities = jpaRepository.findRatesForBrandAndProductAndCurrency( brandId, productId, currency, date );
        log.debug( "JPA repository returns {} entities. {} {} {} {}",
            entities.size(),
            kv( "brandId", brandId ),
            kv( "productId", productId ),
            kv( "currency", currency ),
            kv( "date", date ) );

        return entities.stream().map( mapper::rateJpaEntityToRate ).toList();
    }

    @Override
    public List<Rate> findRatesForBrandAndProduct( final Integer brandId, final Long productId,
        final Instant date )
    {
        log.debug( "Finding rates. {} {} {}",
            kv( "brandId", brandId ),
            kv( "productId", productId ),
            kv( "date", date ) );

        var entities = jpaRepository.findRatesForBrandAndProduct( brandId, productId, date );
        log.debug( "JPA repository returns {} entities. {} {} {}",
            entities.size(),
            kv( "brandId", brandId ),
            kv( "productId", productId ),
            kv( "date", date ) );

        return entities.stream().map( mapper::rateJpaEntityToRate ).toList();
    }
}
