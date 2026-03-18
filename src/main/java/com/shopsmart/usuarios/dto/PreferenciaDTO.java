package com.shopsmart.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * DTOs relacionados con preferencias del usuario.
 */
public class PreferenciaDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "Preferencias del usuario para personalización")
    public static class PreferenciaRequest {
        private List<String> categoriasFavoritas;
        private Boolean notifEmail;
        private Boolean notifSms;
        private Boolean notifPromociones;
        @Schema(example = "es", description = "Código de idioma ISO 639-1")
        private String idioma;
        @Schema(example = "CLP")
        private String moneda;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "Preferencias actuales del usuario")
    public static class PreferenciaResponse {
        private List<String> categoriasFavoritas;
        private Boolean notifEmail;
        private Boolean notifSms;
        private Boolean notifPromociones;
        private String idioma;
        private String moneda;
    }
}
