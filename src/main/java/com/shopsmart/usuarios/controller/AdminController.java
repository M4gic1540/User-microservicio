package com.shopsmart.usuarios.controller;

import com.shopsmart.usuarios.dto.UsuarioDTO;
import com.shopsmart.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints administrativos para gestion de usuarios.
 */
@RestController
@RequestMapping("/api/v1/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Usuarios", description = "Operaciones administrativas sobre usuarios")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios activos (paginado)")
    public ResponseEntity<Page<UsuarioDTO.UsuarioResponse>> listar(
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarUsuarios(pageable));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuarios por nombre, apellido o email")
    public ResponseEntity<Page<UsuarioDTO.UsuarioResponse>> buscar(
            @Parameter(description = "Término de búsqueda")
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(usuarioService.buscarUsuarios(q, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UsuarioDTO.UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar usuario por ID")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
