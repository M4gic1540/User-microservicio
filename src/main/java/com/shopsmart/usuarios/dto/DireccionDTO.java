package com.shopsmart.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTOs relacionados con direcciones de envio.
 */
public class DireccionDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "Datos para crear o editar una dirección de envío")
    public static class DireccionRequest {

        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 200)
        @Schema(example = "Av. Providencia 1234, Depto 5B")
        private String calle;

        @NotBlank
        @Schema(example = "Santiago")
        private String ciudad;

        @NotBlank
        @Schema(example = "Región Metropolitana")
        private String region;

        @NotBlank
        @Schema(example = "7500000")
        private String codigoPostal;

        @NotBlank
        @Schema(example = "Chile")
        private String pais;

        @Builder.Default
        private Boolean esPrincipal = false;

        @Size(max = 100)
        @Schema(example = "Casa", description = "Alias opcional para identificar la dirección")
        private String alias;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "Dirección de envío registrada")
    public static class DireccionResponse {
        private Long id;
        private String calle;
        private String ciudad;
        private String region;
        private String codigoPostal;
        private String pais;
        private Boolean esPrincipal;
        private String alias;
    }
}
