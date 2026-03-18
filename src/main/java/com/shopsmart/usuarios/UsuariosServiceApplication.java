package com.shopsmart.usuarios;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio de usuarios y definición base de OpenAPI.
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "ShopSmart - Servicio de Usuarios",
        version = "1.0.0",
        description = "Microservicio responsable de la gestión de usuarios, autenticación JWT, " +
                      "perfiles y personalización de experiencia en ShopSmart.",
        contact = @Contact(name = "Equipo ShopSmart", email = "dev@shopsmart.com")
    )
)
public class UsuariosServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsuariosServiceApplication.class, args);
    }
}
