package com.mgm.inditex.adapter.inbound.web.controller.rate.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.mgm.inditex.controller.model.PriceResponse;
import com.mgm.inditex.core.domain.Rate;

/**
 * Please add your description here.
 *
 * @author Miguel Maquieira
 */
@Mapper( componentModel = "spring" )
public interface RateWebMapper
{

    // Domain → API
    @Mappings( {
        @Mapping( target = "brandId",       source = "brandId" ),
        @Mapping( target = "productId",     source = "productId" ),
        @Mapping( target = "priceList",     source = "priceListId" ),
        @Mapping( target = "startDate",     source = "startDate" ),
        @Mapping( target = "endDate",       source = "endDate" ),
        @Mapping( target = "price",         source = "price", qualifiedByName = "round2" ),
        @Mapping( target = "currency",      source = "currency" )
    } )
    PriceResponse domainToApi( Rate rate );

    default OffsetDateTime mapToOffset( final LocalDateTime localDateTime )
    {
        if ( localDateTime == null )
        {
            return null;
        }
        return localDateTime.atOffset( ZoneOffset.UTC );
    }

    @Named( "round2" )
    default BigDecimal round2( final BigDecimal value )
    {
        return value == null ? null : value.setScale( 2, RoundingMode.HALF_UP );
    }
}
