package com.mgm.inditex.adapter.outbound.persistence.jpa.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/**
 * Represents a price for a product, including details like price list, dates, and currency.
 *
 * @author Miguel Maquieira
 */
@Data
@Entity
@Table(
    name = "rates",
    uniqueConstraints = {
        @UniqueConstraint( columnNames = {"brand_id", "start_date", "end_date", "product_id", "currency"} )
    } )
public class RateJpaEntity
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY ) // Auto-generated ID
    private Long id;

    @Column( name = "brand_id", nullable = false )
    private Integer brandId;

    @Column( name = "product_id", nullable = false )
    private Long productId;

    @Column( name = "currency", length = 3, nullable = false )
    private String currency;

    @Column( name = "price_list_id", nullable = false )
    private Integer priceListId;

    @Column( name = "start_date", nullable = false )
    private Instant startDate;

    @Column( name = "end_date", nullable = false )
    private Instant endDate;

    private Short priority;

    @Column( name = "price", precision = 10, scale = 4 )
    private BigDecimal price;

}
