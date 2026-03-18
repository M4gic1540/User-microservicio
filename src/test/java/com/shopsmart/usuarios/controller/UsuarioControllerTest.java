package com.shopsmart.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.usuarios.dto.DireccionDTO;
import com.shopsmart.usuarios.dto.PreferenciaDTO;
import com.shopsmart.usuarios.dto.UsuarioDTO;
import com.shopsmart.usuarios.config.JwtAuthFilter;
import com.shopsmart.usuarios.config.SecurityConfig;
import com.shopsmart.usuarios.exception.GlobalExceptionHandler;
import com.shopsmart.usuarios.service.UsuarioService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private UsuarioDTO.UsuarioResponse usuarioResponse;

    private RequestPostProcessor authUser() {
        return user("maria@test.com").roles("CLIENTE");
    }

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());

        usuarioResponse = UsuarioDTO.UsuarioResponse.builder()
            .id(10L)
            .nombre("Maria")
            .apellido("Gonzalez")
            .email("maria@test.com")
            .telefono("+56912345678")
            .rol("CLIENTE")
            .activo(true)
            .build();
    }

    @Test
    @DisplayName("GET /usuarios/me retorna perfil del usuario autenticado")
    void miPerfil_retornaPerfil() throws Exception {
        when(usuarioService.buscarPorEmail("maria@test.com")).thenReturn(usuarioResponse);

        mockMvc.perform(get("/api/v1/usuarios/me").with(authUser()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("maria@test.com"));
    }

    @Test
    @DisplayName("PUT /usuarios/me actualiza perfil")
    void actualizarPerfil_actualizaDatos() throws Exception {
        UsuarioDTO.ActualizarPerfilRequest request = UsuarioDTO.ActualizarPerfilRequest.builder()
            .nombre("Maria")
            .apellido("Gonzalez")
            .telefono("+56912345678")
            .build();

        when(usuarioService.obtenerIdPorEmail("maria@test.com")).thenReturn(10L);
        when(usuarioService.actualizarPerfil(eq(10L), any())).thenReturn(usuarioResponse);

        mockMvc.perform(put("/api/v1/usuarios/me")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("PUT /usuarios/me valida datos invalidos")
    void actualizarPerfil_conDatosInvalidos_retorna400() throws Exception {
        UsuarioDTO.ActualizarPerfilRequest request = UsuarioDTO.ActualizarPerfilRequest.builder()
            .nombre("A")
            .telefono("abc")
            .build();

        mockMvc.perform(put("/api/v1/usuarios/me")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /usuarios/me desactiva cuenta")
    void desactivarCuenta_desactiva() throws Exception {
        when(usuarioService.obtenerIdPorEmail("maria@test.com")).thenReturn(10L);
        doNothing().when(usuarioService).desactivarUsuario(10L);

        mockMvc.perform(delete("/api/v1/usuarios/me").with(authUser()))
            .andExpect(status().isNoContent());

        verify(usuarioService).desactivarUsuario(10L);
    }

    @Test
    @DisplayName("GET /usuarios/me/direcciones lista direcciones")
    void listarDirecciones_retornaLista() throws Exception {
        DireccionDTO.DireccionResponse direccion = DireccionDTO.DireccionResponse.builder()
            .id(1L)
            .calle("Av. Siempre Viva 123")
            .ciudad("Santiago")
            .region("RM")
            .codigoPostal("7500000")
            .pais("Chile")
            .esPrincipal(true)
            .alias("Casa")
            .build();

        when(usuarioService.obtenerIdPorEmail("maria@test.com")).thenReturn(10L);
        when(usuarioService.obtenerDirecciones(10L)).thenReturn(List.of(direccion));

        mockMvc.perform(get("/api/v1/usuarios/me/direcciones").with(authUser()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].pais").value("Chile"));
    }

    @Test
    @DisplayName("POST /usuarios/me/direcciones agrega direccion")
    void agregarDireccion_retornaCreado() throws Exception {
        DireccionDTO.DireccionRequest request = DireccionDTO.DireccionRequest.builder()
            .calle("Av. Siempre Viva 123")
            .ciudad("Santiago")
            .region("RM")
            .codigoPostal("7500000")
            .pais("Chile")
            .esPrincipal(true)
            .alias("Casa")
            .build();

        DireccionDTO.DireccionResponse response = DireccionDTO.DireccionResponse.builder()
            .id(5L)
            .calle("Av. Siempre Viva 123")
            .ciudad("Santiago")
            .region("RM")
            .codigoPostal("7500000")
            .pais("Chile")
            .esPrincipal(true)
            .alias("Casa")
            .build();

        when(usuarioService.obtenerIdPorEmail("maria@test.com")).thenReturn(10L);
        when(usuarioService.agregarDireccion(eq(10L), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/usuarios/me/direcciones")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    @DisplayName("DELETE /usuarios/me/direcciones/{id} elimina direccion")
    void eliminarDireccion_retorna204() throws Exception {
        when(usuarioService.obtenerIdPorEmail("maria@test.com")).thenReturn(10L);
        doNothing().when(usuarioService).eliminarDireccion(10L, 7L);

        mockMvc.perform(delete("/api/v1/usuarios/me/direcciones/7").with(authUser()))
            .andExpect(status().isNoContent());

        verify(usuarioService).eliminarDireccion(10L, 7L);
    }

    @Test
    @DisplayName("GET /usuarios/me/preferencias retorna preferencias")
    void obtenerPreferencias_retornaPreferencias() throws Exception {
        PreferenciaDTO.PreferenciaResponse response = PreferenciaDTO.PreferenciaResponse.builder()
            .categoriasFavoritas(List.of("tech", "hogar"))
            .notifEmail(true)
            .notifSms(false)
            .notifPromociones(true)
            .idioma("es")
            .moneda("CLP")
            .build();

        when(usuarioService.obtenerIdPorEmail("maria@test.com")).thenReturn(10L);
        when(usuarioService.obtenerPreferencias(10L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/usuarios/me/preferencias").with(authUser()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idioma").value("es"));
    }

    @Test
    @DisplayName("PUT /usuarios/me/preferencias actualiza preferencias")
    void actualizarPreferencias_retornaPreferencias() throws Exception {
        PreferenciaDTO.PreferenciaRequest request = PreferenciaDTO.PreferenciaRequest.builder()
            .categoriasFavoritas(List.of("tech", "hogar"))
            .notifEmail(true)
            .notifSms(false)
            .notifPromociones(true)
            .idioma("es")
            .moneda("CLP")
            .build();

        PreferenciaDTO.PreferenciaResponse response = PreferenciaDTO.PreferenciaResponse.builder()
            .categoriasFavoritas(List.of("tech", "hogar"))
            .notifEmail(true)
            .notifSms(false)
            .notifPromociones(true)
            .idioma("es")
            .moneda("CLP")
            .build();

        when(usuarioService.obtenerIdPorEmail("maria@test.com")).thenReturn(10L);
        when(usuarioService.actualizarPreferencias(eq(10L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/usuarios/me/preferencias")
                .with(authUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.moneda").value("CLP"));
    }
}
