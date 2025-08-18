package com.mgm.inditex.infrastructure.security;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Allowed Origin config bean.
 *
 * @author Miguel Maquieira
 */
@Component
@ConfigurationProperties( prefix = "app.security" )
@Data
public class SecurityConfigProps
{
    private List<String> allowedOrigins;
    private List<String> whitelistedPaths;
}
