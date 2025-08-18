package com.mgm.inditex.infrastructure.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mgm.inditex.infrastructure.security.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Security config.
 *
 * @author Miguel Maquieira
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig
{
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityConfigProps securityConfigProps;

    @Bean
    public SecurityFilterChain securityFilterChain( final HttpSecurity http ) throws Exception
    {
        http.csrf( AbstractHttpConfigurer::disable )
            .cors( cors -> cors.configurationSource( corsConfigurationSource() ) )
            .authorizeHttpRequests(
                auth -> auth.requestMatchers( securityConfigProps.getWhitelistedPaths().toArray( String[]::new ) )
                    .permitAll()
                    .anyRequest()
                    .authenticated() )
            .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )
            .addFilterBefore( jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins( securityConfigProps.getAllowedOrigins() );
        config.setAllowedMethods( List.of( "GET", "POST", "PUT", "DELETE", "OPTIONS" ) );
        config.setAllowedHeaders( List.of( "*" ) );
        config.setAllowCredentials( true );

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration( "/**", config );
        return source;
    }

}
