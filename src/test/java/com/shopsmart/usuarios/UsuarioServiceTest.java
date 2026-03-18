package com.shopsmart.usuarios;

import com.shopsmart.usuarios.dto.UsuarioDTO;
import com.shopsmart.usuarios.exception.ShopSmartException;
import com.shopsmart.usuarios.model.Usuario;
import com.shopsmart.usuarios.repository.DireccionRepository;
import com.shopsmart.usuarios.repository.PreferenciaRepository;
import com.shopsmart.usuarios.repository.UsuarioRepository;
import com.shopsmart.usuarios.config.JwtUtil;
import com.shopsmart.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del Servicio de Usuarios")
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepo;
    @Mock private DireccionRepository direccionRepo;
    @Mock private PreferenciaRepository preferenciaRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authManager;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioDTO.RegistroRequest registroRequest;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        registroRequest = UsuarioDTO.RegistroRequest.builder()
            .nombre("María")
            .apellido("González")
            .email("maria@test.com")
            .password("Segura123")
            .build();

        usuarioMock = Usuario.builder()
            .id(1L)
            .nombre("María")
            .apellido("González")
            .email("maria@test.com")
            .password("$2a$10$encoded")
            .activo(true)
            .rol(Usuario.Rol.CLIENTE)
            .build();
    }

    @Test
    @DisplayName("Registro exitoso de nuevo usuario")
    void registro_conDatosValidos_creaUsuario() {
        when(usuarioRepo.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(usuarioRepo.save(any())).thenReturn(usuarioMock);
        when(userDetailsService.loadUserByUsername(anyString()))
            .thenReturn(org.springframework.security.core.userdetails.User
                .withUsername("maria@test.com").password("encoded").roles("CLIENTE").build());
        when(jwtUtil.generarToken(any())).thenReturn("access-token");
        when(jwtUtil.generarRefreshToken(any())).thenReturn("refresh-token");
        when(jwtUtil.getExpiration()).thenReturn(86400000L);

        UsuarioDTO.AuthResponse response = usuarioService.registrar(registroRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getUsuario().getEmail()).isEqualTo("maria@test.com");
        verify(usuarioRepo).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registro falla si email ya existe")
    void registro_conEmailDuplicado_lanzaExcepcion() {
        when(usuarioRepo.existsByEmail("maria@test.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrar(registroRequest))
            .isInstanceOf(ShopSmartException.EmailYaRegistrado.class)
            .hasMessageContaining("maria@test.com");

        verify(usuarioRepo, never()).save(any());
    }

    @Test
    @DisplayName("Obtener perfil por ID existente")
    void obtenerPerfil_conIdValido_retornaUsuario() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuarioMock));

        UsuarioDTO.UsuarioResponse response = usuarioService.obtenerPerfil(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("María");
        assertThat(response.getEmail()).isEqualTo("maria@test.com");
    }

    @Test
    @DisplayName("Obtener perfil con ID inexistente lanza excepción")
    void obtenerPerfil_conIdInexistente_lanzaExcepcion() {
        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerPerfil(99L))
            .isInstanceOf(ShopSmartException.UsuarioNoEncontrado.class);
    }

    @Test
    @DisplayName("Desactivar usuario cambia estado a false")
    void desactivarUsuario_conIdValido_desactiva() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepo.save(any())).thenReturn(usuarioMock);

        usuarioService.desactivarUsuario(1L);

        assertThat(usuarioMock.getActivo()).isFalse();
        verify(usuarioRepo).save(usuarioMock);
    }
}
