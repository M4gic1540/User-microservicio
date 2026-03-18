package com.shopsmart.usuarios.controller;

import com.shopsmart.usuarios.dto.UsuarioDTO;
import com.shopsmart.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de autenticacion para registro e inicio de sesion.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro e inicio de sesión de usuarios")
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario",
               description = "Crea una cuenta nueva y retorna tokens JWT de acceso")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    public ResponseEntity<UsuarioDTO.AuthResponse> registrar(
            @Valid @RequestBody UsuarioDTO.RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
               description = "Autentica las credenciales y retorna tokens JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    public ResponseEntity<UsuarioDTO.AuthResponse> login(
            @Valid @RequestBody UsuarioDTO.LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }
}
