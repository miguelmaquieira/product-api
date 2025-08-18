package com.mgm.inditex.adapter.outbound.persistence.jpa;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mgm.inditex.adapter.outbound.persistence.jpa.entity.RateJpaEntity;

/**
 * Repository for managing price data in the database.
 * This interface extends JpaRepository to provide basic CRUD operations and custom queries.
 *
 * @author Miguel Maquieira
 */
public interface RateJpaRepository extends JpaRepository<RateJpaEntity, Long>
{
    // Query to find prices when currency is provided
    @Query( "SELECT r FROM RateJpaEntity r WHERE r.brandId = :brandId " +
        "AND r.productId = :productId " +
        "AND r.currency = :currency " +
        "AND r.startDate <= :date " +
        "AND r.endDate >= :date" )
    List<RateJpaEntity> findRatesForBrandAndProductAndCurrency(
        @Param( "brandId" ) Integer brandId,
        @Param( "productId" ) Long productId,
        @Param( "currency" ) String currency,
        @Param( "date" ) Instant date );

    // Query to find prices when currency is not provided (returns a list of prices)
    @Query( "SELECT r FROM RateJpaEntity r WHERE r.brandId = :brandId " +
        "AND r.productId = :productId " +
        "AND r.startDate <= :date " +
        "AND r.endDate >= :date" )
    List<RateJpaEntity> findRatesForBrandAndProduct(
        @Param( "brandId" ) Integer brandId,
        @Param( "productId" ) Long productId,
        @Param( "date" ) Instant date );
}
