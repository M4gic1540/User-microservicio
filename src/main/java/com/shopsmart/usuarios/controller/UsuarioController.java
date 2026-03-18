package com.shopsmart.usuarios.controller;

import com.shopsmart.usuarios.dto.DireccionDTO;
import com.shopsmart.usuarios.dto.PreferenciaDTO;
import com.shopsmart.usuarios.dto.UsuarioDTO;
import com.shopsmart.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para que el usuario gestione su perfil, direcciones y preferencias.
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de perfil, direcciones y preferencias")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ─── Perfil ──────────────────────────────────────────────────

    @GetMapping("/me")
    @Operation(summary = "Obtener mi perfil", description = "Retorna el perfil del usuario autenticado")
    public ResponseEntity<UsuarioDTO.UsuarioResponse> miPerfil(
            @AuthenticationPrincipal UserDetails userDetails) {
        // Obtenemos el usuario por email desde el token
        return ResponseEntity.ok(
            usuarioService.buscarPorEmail(userDetails.getUsername())
        );
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar mi perfil")
    public ResponseEntity<UsuarioDTO.UsuarioResponse> actualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UsuarioDTO.ActualizarPerfilRequest request) {
        Long id = usuarioService.obtenerIdPorEmail(userDetails.getUsername());
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, request));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Desactivar mi cuenta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivarCuenta(@AuthenticationPrincipal UserDetails userDetails) {
        Long id = usuarioService.obtenerIdPorEmail(userDetails.getUsername());
        usuarioService.desactivarUsuario(id);
    }

    // ─── Direcciones ──────────────────────────────────────────────

    @GetMapping("/me/direcciones")
    @Operation(summary = "Listar mis direcciones de envío")
    public ResponseEntity<List<DireccionDTO.DireccionResponse>> listarDirecciones(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long id = usuarioService.obtenerIdPorEmail(userDetails.getUsername());
        return ResponseEntity.ok(usuarioService.obtenerDirecciones(id));
    }

    @PostMapping("/me/direcciones")
    @Operation(summary = "Agregar dirección de envío")
    public ResponseEntity<DireccionDTO.DireccionResponse> agregarDireccion(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DireccionDTO.DireccionRequest request) {
        Long id = usuarioService.obtenerIdPorEmail(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.agregarDireccion(id, request));
    }

    @DeleteMapping("/me/direcciones/{direccionId}")
    @Operation(summary = "Eliminar dirección de envío")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarDireccion(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de la dirección a eliminar")
            @PathVariable Long direccionId) {
        Long id = usuarioService.obtenerIdPorEmail(userDetails.getUsername());
        usuarioService.eliminarDireccion(id, direccionId);
    }

    // ─── Preferencias ─────────────────────────────────────────────

    @GetMapping("/me/preferencias")
    @Operation(summary = "Obtener mis preferencias de personalización")
    public ResponseEntity<PreferenciaDTO.PreferenciaResponse> obtenerPreferencias(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long id = usuarioService.obtenerIdPorEmail(userDetails.getUsername());
        return ResponseEntity.ok(usuarioService.obtenerPreferencias(id));
    }

    @PutMapping("/me/preferencias")
    @Operation(summary = "Actualizar mis preferencias")
    public ResponseEntity<PreferenciaDTO.PreferenciaResponse> actualizarPreferencias(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PreferenciaDTO.PreferenciaRequest request) {
        Long id = usuarioService.obtenerIdPorEmail(userDetails.getUsername());
        return ResponseEntity.ok(usuarioService.actualizarPreferencias(id, request));
    }
}
