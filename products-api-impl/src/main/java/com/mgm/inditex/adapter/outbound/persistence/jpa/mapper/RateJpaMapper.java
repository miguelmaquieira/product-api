package com.mgm.inditex.adapter.outbound.persistence.jpa.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;

import com.mgm.inditex.adapter.outbound.persistence.jpa.entity.RateJpaEntity;
import com.mgm.inditex.core.domain.Rate;

/**
 * MapStruct mapper for converting between JPA persistence entities
 * ({@link RateJpaEntity})
 * and domain model objects ({@link Rate}).
 * <p>
 * Handles type conversions such as {@link Instant} to {@link LocalDateTime}
 * and normalizes monetary values by rounding {@link BigDecimal} to 2 decimals.
 * </p>
 *
 * @author Miguel Maquieira
 */
@Mapper( componentModel = "spring" )
public interface RateJpaMapper
{

    // entity -> domain
    Rate rateJpaEntityToRate( RateJpaEntity rateJpaEntity );

    // Tell MapStruct how to build a Rate using the domain factory
    @ObjectFactory
    default Rate newRate( @TargetType final Class<Rate> type, final RateJpaEntity src )
    {
        if ( src == null )
        {
            return null;
        }
        return Rate.of(
            src.getBrandId(),
            src.getProductId(),
            src.getPriceListId(),
            map( src.getStartDate() ),
            map( src.getEndDate() ),
            src.getPriority(),
            src.getPrice(),
            src.getCurrency()
        );
    }

    // Custom mapping method to convert Instant to LocalDateTime
    default LocalDateTime map( final Instant instant )
    {
        return instant == null ? null : LocalDateTime.ofInstant( instant, ZoneOffset.UTC );
    }

    default BigDecimal round2( final BigDecimal value )
    {
        return value == null ? null : value.setScale( 2, RoundingMode.HALF_UP );
    }
}
