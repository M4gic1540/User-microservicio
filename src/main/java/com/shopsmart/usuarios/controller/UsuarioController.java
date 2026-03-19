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
    @Operation(
        summary = "Listar usuarios públicos",
        description = "Retorna usuarios activos sin autenticación",
        operationId = "01_get_usuarios"
    )
    public ResponseEntity<java.util.Map<String, Object>> listarPublico(
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        Pageable safePageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by("fechaCreacion").descending()
        );
        Page<UsuarioDTO.UsuarioResponse> usuarios = usuarioService.listarUsuarios(safePageable);
        return ResponseEntity.ok(java.util.Map.of(
            "codigo", "USUARIOS_LISTADOS",
            "mensaje", "Usuarios listados correctamente",
            "data", usuarios
        ));
    }

    @PostMapping
    @Operation(
        summary = "Registrar usuario público",
        description = "Crea un usuario sin JWT ni login",
        operationId = "02_post_usuarios"
    )
    public ResponseEntity<java.util.Map<String, Object>> registrarPublico(
            @Valid @RequestBody UsuarioDTO.RegistroRequest request) {
        UsuarioDTO.UsuarioResponse usuario = usuarioService.registrarPublico(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(java.util.Map.of(
                    "codigo", "USUARIO_CREADO",
                    "mensaje", "Usuario creado",
                    "data", usuario
                ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar usuario por ID",
        description = "Actualiza datos básicos del usuario",
        operationId = "03_put_usuarios_id"
    )
    public ResponseEntity<java.util.Map<String, Object>> actualizarPorId(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.ActualizarPerfilRequest request) {
        UsuarioDTO.UsuarioResponse usuario = usuarioService.actualizarPerfil(id, request);
        return ResponseEntity.ok(java.util.Map.of(
            "codigo", "USUARIO_ACTUALIZADO",
            "mensaje", "Usuario actualizado correctamente",
            "data", usuario
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar usuario por ID",
        description = "Desactiva el usuario por su ID",
        operationId = "04_delete_usuarios_id"
    )
    public ResponseEntity<java.util.Map<String, String>> desactivarPorId(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok(java.util.Map.of(
            "codigo", "USUARIO_DESACTIVADO",
            "mensaje", "Usuario desactivado correctamente"
        ));
    }
}
