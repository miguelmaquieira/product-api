package com.mgm.inditex.infrastructure.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filter that intercepts incoming HTTP requests to perform JWT-based authentication.
 * <p>
 * This filter extracts the JWT token from the {@code Authorization} header,
 * validates it, and populates the Spring Security {@link SecurityContextHolder}
 * with an authenticated {@link UsernamePasswordAuthenticationToken} if valid.
 * </p>
 *
 * @author Miguel Maquieira
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal( final HttpServletRequest request, final HttpServletResponse response,
        final FilterChain filterChain ) throws ServletException, IOException
    {

        final String authHeader = request.getHeader( "Authorization" );
        String username = null;

        if ( authHeader != null && authHeader.startsWith( "Bearer " ) )
        {
            String token = authHeader.substring( 7 );
            try
            {
                username = jwtService.validateAndGetSubject( token );
            }
            catch ( Exception e )
            {
                response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
                return;
            }
        }

        if ( username != null && SecurityContextHolder.getContext().getAuthentication() == null )
        {
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken( username, null, null );
            authentication.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );
            SecurityContextHolder.getContext().setAuthentication( authentication );
        }
        filterChain.doFilter( request, response );
    }
}
