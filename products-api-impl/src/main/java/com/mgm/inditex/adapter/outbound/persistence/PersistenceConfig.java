package com.mgm.inditex.adapter.outbound.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mgm.inditex.adapter.outbound.persistence.jpa.RateJpaRepository;
import com.mgm.inditex.adapter.outbound.persistence.jpa.mapper.RateJpaMapper;
import com.mgm.inditex.core.port.outbound.RateRepositoryPort;

/**
 * Spring configuration for persistence layer beans.
 *
 * @author Miguel Maquieira
 */
@Configuration
public class PersistenceConfig
{
    @Bean
    RateRepositoryPort rateRepositoryPort( final RateJpaRepository jpa, final RateJpaMapper jpaMapper )
    {
        return new RateJpaRepositoryAdapter( jpa,  jpaMapper );
    }
}
