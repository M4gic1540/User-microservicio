package com.shopsmart.usuarios.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excepcion base del dominio con codigo y estado HTTP asociado.
 */
@Getter
public class ShopSmartException extends RuntimeException {

    private final HttpStatus status;
    private final String codigo;

    public ShopSmartException(String message, HttpStatus status, String codigo) {
        super(message);
        this.status = status;
        this.codigo = codigo;
    }

    // ─── Excepciones específicas ──────────────────────────────────

    public static class UsuarioNoEncontrado extends ShopSmartException {
        public UsuarioNoEncontrado(Long id) {
            super("Usuario con ID " + id + " no encontrado", HttpStatus.NOT_FOUND, "USUARIO_NO_ENCONTRADO");
        }
        public UsuarioNoEncontrado(String email) {
            super("Usuario con email " + email + " no encontrado", HttpStatus.NOT_FOUND, "USUARIO_NO_ENCONTRADO");
        }
    }

    public static class EmailYaRegistrado extends ShopSmartException {
        public EmailYaRegistrado(String email) {
            super("El email " + email + " ya está registrado", HttpStatus.CONFLICT, "EMAIL_DUPLICADO");
        }
    }

    public static class CredencialesInvalidas extends ShopSmartException {
        public CredencialesInvalidas() {
            super("Email o contraseña incorrectos", HttpStatus.UNAUTHORIZED, "CREDENCIALES_INVALIDAS");
        }
    }

    public static class DireccionNoEncontrada extends ShopSmartException {
        public DireccionNoEncontrada(Long id) {
            super("Dirección con ID " + id + " no encontrada", HttpStatus.NOT_FOUND, "DIRECCION_NO_ENCONTRADA");
        }
    }

    public static class AccesoDenegado extends ShopSmartException {
        public AccesoDenegado() {
            super("No tienes permisos para realizar esta acción", HttpStatus.FORBIDDEN, "ACCESO_DENEGADO");
        }
    }
}
