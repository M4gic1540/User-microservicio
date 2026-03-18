package com.shopsmart.usuarios.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de errores para respuestas consistentes en la API.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ShopSmartException.class)
    public ResponseEntity<ErrorResponse> handleShopSmartException(ShopSmartException ex) {
        log.warn("ShopSmartException: {} - {}", ex.getCodigo(), ex.getMessage());
        return ResponseEntity
            .status(ex.getStatus())
            .body(new ErrorResponse(ex.getCodigo(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(e -> {
            String campo = ((FieldError) e).getField();
            errores.put(campo, e.getDefaultMessage());
        });
        ErrorResponse response = new ErrorResponse("VALIDACION_FALLIDA", "Datos de entrada inválidos");
        response.setDetalles(errores);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAcceso(AccessDeniedException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("ACCESO_DENEGADO", "No tienes permisos para esta operación"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Error inesperado", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("ERROR_INTERNO", "Ha ocurrido un error inesperado"));
    }

    // ─── Inner class para la respuesta de error ──────────────────
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        private String codigo;
        private String mensaje;
        private LocalDateTime timestamp = LocalDateTime.now();
        private Map<String, String> detalles;

        public ErrorResponse(String codigo, String mensaje) {
            this.codigo = codigo;
            this.mensaje = mensaje;
            this.timestamp = LocalDateTime.now();
        }
    }
}
