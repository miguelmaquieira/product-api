package com.mgm.inditex.infrastructure.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for generating and validating JWT tokens.
 * <p>
 * Uses a configurable secret key and expiration time loaded from application properties.
 * Provides methods for issuing signed tokens and extracting the subject (e.g. username/email)
 * from an incoming token.
 * </p>
 *
 * @author Miguel Maquieira
 */
@Slf4j
@Service
public class JwtService
{
    private final byte[] keyBytes;
    private final long expirationMs;

    public JwtService(
        @Value( "${security.jwt.secret}" ) final String secret,
        @Value( "${security.jwt.expiration-ms:3600000}" ) final long expirationMs
    )
    {
        this.keyBytes = secret.getBytes( StandardCharsets.UTF_8);
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT token for the given subject.
     *
     * @param subject the identifier for the token (e.g. username or email)
     * @return signed JWT string
     */
    public String generateToken( final String subject )
    {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject( subject )
            .setIssuedAt( new Date( now ) )
            .setExpiration( new Date( now + expirationMs ) )
            .signWith( Keys.hmacShaKeyFor( keyBytes ) )
            .compact();
    }

    /**
     * Validates the token and returns its subject if valid.
     *
     * @param token the JWT string
     * @return the subject (e.g. username/email) if valid, or {@code null} if invalid/expired
     */
    public String validateAndGetSubject( final String token )
    {
        try
        {
            return Jwts.parserBuilder()
                .setSigningKey( Keys.hmacShaKeyFor( keyBytes ) )
                .build()
                .parseClaimsJws( token )
                .getBody()
                .getSubject();
        }
        catch (  Exception e )
        {
            return null;
        }
    }

    /**
     * @return configured expiration time in seconds
     */
    public int getExpirationSeconds() {
        return ( int ) ( expirationMs / 1000 );
    }
}
