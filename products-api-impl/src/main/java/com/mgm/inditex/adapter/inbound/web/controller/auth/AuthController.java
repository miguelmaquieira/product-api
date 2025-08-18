package com.mgm.inditex.adapter.inbound.web.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.mgm.inditex.controller.api.AuthApiDelegate;
import com.mgm.inditex.controller.model.JwtResponse;
import com.mgm.inditex.controller.model.LoginRequest;
import com.mgm.inditex.infrastructure.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inbound web adapter that implements the {@link AuthApiDelegate} contract.
 * <p>
 * This controller exposes the authentication endpoint defined in the OpenAPI specification,
 * validating user credentials and issuing JWT tokens.
 * </p>
 *
 * @author Miguel Maquieira
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthController implements AuthApiDelegate
{
    private final JwtService jwtService;

    @Override
    public ResponseEntity<JwtResponse> login( final LoginRequest loginRequest )
    {
        if ( "miguel@inditex.com".equals( loginRequest.getEmail() ) && "password".equals( loginRequest.getPassword() ) )
        {
            String token = jwtService.generateToken( loginRequest.getEmail() );
            return ResponseEntity.ok( new JwtResponse( token ) );
        }
        return ResponseEntity.status( 401 ).build();
    }
}
