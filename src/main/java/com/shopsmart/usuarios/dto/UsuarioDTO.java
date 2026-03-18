package com.shopsmart.usuarios.dto;

import com.shopsmart.usuarios.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTOs de registro, login y representaciones de usuario.
 */
public class UsuarioDTO {

    // ─── REQUEST: Registro ─────────────────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "Datos para registro de nuevo usuario")
    public static class RegistroRequest {

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100)
        @Schema(example = "María")
        private String nombre;

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 100)
        @Schema(example = "González")
        private String apellido;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        @Schema(example = "maria.gonzalez@email.com")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                 message = "La contraseña debe contener mayúsculas, minúsculas y números")
        @Schema(example = "Segura123")
        private String password;

        @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "Formato de teléfono inválido")
        @Schema(example = "+56912345678")
        private String telefono;
    }

    // ─── REQUEST: Login ────────────────────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    @Schema(description = "Credenciales de acceso")
    public static class LoginRequest {

        @NotBlank @Email
        @Schema(example = "maria.gonzalez@email.com")
        private String email;

        @NotBlank
        @Schema(example = "Segura123")
        private String password;
    }

    // ─── REQUEST: Actualizar perfil ────────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "Datos para actualizar perfil")
    public static class ActualizarPerfilRequest {

        @Size(min = 2, max = 100)
        private String nombre;

        @Size(min = 2, max = 100)
        private String apellido;

        @Pattern(regexp = "^[+]?[0-9]{8,15}$")
        private String telefono;
    }

    // ─── RESPONSE: Datos del usuario ───────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "Información del usuario")
    public static class UsuarioResponse {
        private Long id;
        private String nombre;
        private String apellido;
        private String email;
        private String telefono;
        private String rol;
        private Boolean activo;
        private LocalDateTime ultimoAcceso;
        private LocalDateTime fechaCreacion;
        private List<DireccionDTO.DireccionResponse> direcciones;
    }

    // ─── RESPONSE: Autenticación ───────────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "Respuesta tras autenticación exitosa")
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        @Builder.Default
        private String tokenType = "Bearer";
        private Long expiresIn;
        private UsuarioResponse usuario;
    }
}
