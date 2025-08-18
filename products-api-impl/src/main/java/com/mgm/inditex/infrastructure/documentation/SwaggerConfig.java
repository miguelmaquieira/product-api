package com.mgm.inditex.infrastructure.documentation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Swagger/OpenAPI configuration enabled for non-production environments.
 *
 * @author Miguel Maquieira
 */
@Configuration
@Profile( "!prod" )
public class SwaggerConfig
{
    @Bean
    public OpenAPI customOpenAPI()
    {
        return new OpenAPI().info( new Info().title( "Products Price API" )
                .version( "v1" )
                .description( "API documentation for the Product price service" )
                .license( new License().name( "Apache 2.0" ).url( "https://www.apache.org/licenses/LICENSE-2.0.html" ) ) )
            .components( new Components().addSecuritySchemes( "bearerAuth",
                new SecurityScheme()
                    .type( SecurityScheme.Type.HTTP )
                    .scheme( "bearer" ).bearerFormat( "JWT" ) ) )
            .addSecurityItem( new SecurityRequirement().addList( "bearerAuth" ) );
    }
}
