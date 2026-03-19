package com.shopsmart.usuarios.controller;

import com.shopsmart.usuarios.dto.UsuarioDTO;
import com.shopsmart.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints para que el usuario gestione su perfil, direcciones y preferencias.
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de perfil, direcciones y preferencias")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ─── Listado público ─────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar usuarios públicos", description = "Retorna usuarios activos sin autenticación")
    public ResponseEntity<Page<UsuarioDTO.UsuarioResponse>> listarPublico(
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        Pageable safePageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by("fechaCreacion").descending()
        );
        return ResponseEntity.ok(usuarioService.listarUsuarios(safePageable));
    }

    @PostMapping
    @Operation(summary = "Registrar usuario público", description = "Crea un usuario sin JWT ni login")
    public ResponseEntity<UsuarioDTO.UsuarioResponse> registrarPublico(
            @Valid @RequestBody UsuarioDTO.RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrarPublico(request));
    }

    @PutMapping({"/{id}", "/{id}/"})
    @Operation(summary = "Actualizar usuario por ID", description = "Actualiza datos básicos del usuario")
    public ResponseEntity<UsuarioDTO.UsuarioResponse> actualizarPorId(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.ActualizarPerfilRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, request));
    }

    @DeleteMapping({"/{id}", "/{id}/"})
    @Operation(summary = "Desactivar usuario por ID", description = "Desactiva el usuario por su ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivarPorId(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
    }
}
