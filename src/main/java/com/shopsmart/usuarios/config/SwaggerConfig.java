package com.shopsmart.usuarios.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuracion de OpenAPI/Swagger para el microservicio.
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8081}")
    private String port;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .servers(List.of(
                new Server().url("http://localhost:" + port).description("Desarrollo local"),
                new Server().url("http://api-gateway:8080/usuarios").description("Vía API Gateway")
            ))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Introduce el token JWT obtenido en /api/v1/auth/login")
                )
            );
    }
}
