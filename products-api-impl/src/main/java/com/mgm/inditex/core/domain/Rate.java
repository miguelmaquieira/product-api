package com.mgm.inditex.core.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain representation of a product rate (price) within the system.
 * <p>
 * A {@code Rate} defines the applicable price for a product under a brand
 * during a specific validity period. It is immutable and framework-agnostic.
 * </p>
 *
 * @author Miguel Maquieira
 */
public final class Rate
{
    private final Integer brandId;
    private final Long productId;
    private final Integer priceListId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Short priority;
    private final BigDecimal price;
    private final String currency;

    private Rate( final Integer brandId, final Long productId, final Integer priceListId, final LocalDateTime startDate,
        final LocalDateTime endDate, final Short priority, final BigDecimal price, final String currency )
    {
        this.brandId = Objects.requireNonNull( brandId, "brandId" );
        this.productId = Objects.requireNonNull( productId, "productId" );
        this.priceListId = Objects.requireNonNull( priceListId, "priceListId" );
        this.startDate = Objects.requireNonNull( startDate, "startDate" );
        this.endDate = Objects.requireNonNull( endDate, "endDate" );
        this.priority = requireNonNegative( priority );
        this.price = requireNonNegative( requireScale( price, 4 ) );
        this.currency = requireIsoCurrency( currency );

        if ( !startDate.isBefore( endDate ) )
        {
            throw new IllegalArgumentException( "startDate must be strictly before endDate" );
        }
    }

    public static Rate of( final Integer brandId, final Long productId, final Integer priceList,
        final LocalDateTime startDate, final LocalDateTime endDate, final Short priority, final BigDecimal price,
        final String currency )
    {
        return new Rate( brandId, productId, priceList, startDate, endDate, priority, price, currency );
    }

    public boolean appliesAt( final LocalDateTime at )
    {
        return ( at.equals( startDate ) || at.isAfter( startDate ) ) &&
            ( at.equals( endDate ) || at.isBefore( endDate ) );
    }

    // --- Getters (no setters; immutability) ---
    public Integer getBrandId()
    {
        return brandId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public Integer getPriceListId()
    {
        return priceListId;
    }

    public LocalDateTime getStartDate()
    {
        return startDate;
    }

    public LocalDateTime getEndDate()
    {
        return endDate;
    }

    public Short getPriority()
    {
        return priority;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public String getCurrency()
    {
        return currency;
    }

    // --- Equality on business key (brand, product, priceList, interval, currency, price) ---
    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof final Rate that ) )
        {
            return false;
        }
        return Objects.equals( brandId, that.brandId ) && Objects.equals( productId, that.productId ) &&
            Objects.equals( priceListId, that.priceListId ) && Objects.equals( startDate, that.startDate ) &&
            Objects.equals( endDate, that.endDate ) && Objects.equals( currency, that.currency ) &&
            Objects.equals( price, that.price );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( brandId, productId, priceListId, startDate, endDate, currency, price );
    }

    @Override
    public String toString()
    {
        return "Rate{brandId=%d, productId=%d, priceListId=%d, start=%s, end=%s, price=%s %s}".formatted( brandId,
            productId, priceListId, startDate, endDate, price, currency );
    }

    // --- Helpers ---

    private static BigDecimal requireScale( final BigDecimal value, final int scale )
    {
        Objects.requireNonNull( value, "price" );
        return value.setScale( scale, RoundingMode.HALF_UP );
    }

    private static BigDecimal requireNonNegative( final BigDecimal value )
    {
        if ( value.signum() < 0 )
        {
            throw new IllegalArgumentException( "price must be >= 0" );
        }
        return value;
    }

    private static Short requireNonNegative( final Short value )
    {
        Objects.requireNonNull( value, "priority" );
        if ( value < 0 )
        {
            throw new IllegalArgumentException( "priority must be >= 0" );
        }
        return value;
    }

    private static String requireIsoCurrency( final String ccy )
    {
        Objects.requireNonNull( ccy, "currency" );
        if ( ccy.length() != 3 )
        {
            throw new IllegalArgumentException( "currency must be 3-letter ISO code" );
        }
        return ccy.toUpperCase();
    }
}
