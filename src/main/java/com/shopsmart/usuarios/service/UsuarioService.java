package com.shopsmart.usuarios.service;

import com.shopsmart.usuarios.config.JwtUtil;
import com.shopsmart.usuarios.dto.DireccionDTO;
import com.shopsmart.usuarios.dto.PreferenciaDTO;
import com.shopsmart.usuarios.dto.UsuarioDTO;
import com.shopsmart.usuarios.exception.ShopSmartException;
import com.shopsmart.usuarios.model.DireccionEnvio;
import com.shopsmart.usuarios.model.PreferenciaUsuario;
import com.shopsmart.usuarios.model.Usuario;
import com.shopsmart.usuarios.repository.DireccionRepository;
import com.shopsmart.usuarios.repository.PreferenciaRepository;
import com.shopsmart.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio que orquesta registro, login, perfil, direcciones y preferencias.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final DireccionRepository direccionRepo;
    private final PreferenciaRepository preferenciaRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;

    // ─── Registro ────────────────────────────────────────────────

    @Transactional
    public UsuarioDTO.AuthResponse registrar(UsuarioDTO.RegistroRequest request) {
        if (usuarioRepo.existsByEmail(request.getEmail())) {
            throw new ShopSmartException.EmailYaRegistrado(request.getEmail());
        }

        Usuario usuario = Usuario.builder()
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .email(request.getEmail().toLowerCase().trim())
            .password(passwordEncoder.encode(request.getPassword()))
            .telefono(request.getTelefono())
            .build();

        // Crear preferencias por defecto
        PreferenciaUsuario preferencias = PreferenciaUsuario.builder()
            .usuario(usuario)
            .build();
        usuario.setPreferencias(preferencias);

        usuarioRepo.save(usuario);
        log.info("Nuevo usuario registrado: {}", usuario.getEmail());

        return generarAuthResponse(usuario);
    }

    // ─── Login ────────────────────────────────────────────────────

    @Transactional
    public UsuarioDTO.AuthResponse login(UsuarioDTO.LoginRequest request) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new ShopSmartException.CredencialesInvalidas();
        }

        Usuario usuario = usuarioRepo.findByEmail(request.getEmail())
            .orElseThrow(() -> new ShopSmartException.UsuarioNoEncontrado(request.getEmail()));

        usuarioRepo.actualizarUltimoAcceso(usuario.getId(), LocalDateTime.now());
        log.info("Login exitoso: {}", usuario.getEmail());

        return generarAuthResponse(usuario);
    }

    // ─── Consulta de perfil ───────────────────────────────────────

    @Transactional(readOnly = true)
    public UsuarioDTO.UsuarioResponse obtenerPerfil(Long id) {
        Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() -> new ShopSmartException.UsuarioNoEncontrado(id));
        return mapearUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioDTO.UsuarioResponse> listarUsuarios(Pageable pageable) {
        return usuarioRepo.findByActivoTrue(pageable).map(this::mapearUsuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioDTO.UsuarioResponse> buscarUsuarios(String termino, Pageable pageable) {
        return usuarioRepo
            .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
                termino, termino, termino, pageable)
            .map(this::mapearUsuario);
    }

    // ─── Actualización de perfil ──────────────────────────────────

    @Transactional
    public UsuarioDTO.UsuarioResponse actualizarPerfil(Long id, UsuarioDTO.ActualizarPerfilRequest request) {
        Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() -> new ShopSmartException.UsuarioNoEncontrado(id));

        if (request.getNombre() != null) usuario.setNombre(request.getNombre());
        if (request.getApellido() != null) usuario.setApellido(request.getApellido());
        if (request.getTelefono() != null) usuario.setTelefono(request.getTelefono());

        return mapearUsuario(usuarioRepo.save(usuario));
    }

    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() -> new ShopSmartException.UsuarioNoEncontrado(id));
        usuario.setActivo(false);
        usuarioRepo.save(usuario);
        log.info("Usuario desactivado: {}", usuario.getEmail());
    }

    // ─── Direcciones ──────────────────────────────────────────────

    @Transactional
    public DireccionDTO.DireccionResponse agregarDireccion(Long usuarioId, DireccionDTO.DireccionRequest request) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new ShopSmartException.UsuarioNoEncontrado(usuarioId));

        if (request.getEsPrincipal()) {
            direccionRepo.desmarcarTodoPrincipal(usuarioId);
        }

        DireccionEnvio direccion = DireccionEnvio.builder()
            .usuario(usuario)
            .calle(request.getCalle())
            .ciudad(request.getCiudad())
            .region(request.getRegion())
            .codigoPostal(request.getCodigoPostal())
            .pais(request.getPais())
            .esPrincipal(request.getEsPrincipal())
            .alias(request.getAlias())
            .build();

        return mapearDireccion(direccionRepo.save(direccion));
    }

    @Transactional(readOnly = true)
    public List<DireccionDTO.DireccionResponse> obtenerDirecciones(Long usuarioId) {
        return direccionRepo.findByUsuarioId(usuarioId)
            .stream().map(this::mapearDireccion).collect(Collectors.toList());
    }

    @Transactional
    public void eliminarDireccion(Long usuarioId, Long direccionId) {
        DireccionEnvio direccion = direccionRepo.findByIdAndUsuarioId(direccionId, usuarioId)
            .orElseThrow(() -> new ShopSmartException.DireccionNoEncontrada(direccionId));
        direccionRepo.delete(direccion);
    }

    // ─── Preferencias ─────────────────────────────────────────────

    @Transactional
    public PreferenciaDTO.PreferenciaResponse actualizarPreferencias(Long usuarioId, PreferenciaDTO.PreferenciaRequest request) {
        PreferenciaUsuario prefs = preferenciaRepo.findByUsuarioId(usuarioId)
            .orElseGet(() -> {
                Usuario usuario = usuarioRepo.findById(usuarioId)
                    .orElseThrow(() -> new ShopSmartException.UsuarioNoEncontrado(usuarioId));
                return PreferenciaUsuario.builder().usuario(usuario).build();
            });

        if (request.getCategoriasFavoritas() != null) {
            prefs.setCategoriasFavoritas(String.join(",", request.getCategoriasFavoritas()));
        }
        if (request.getNotifEmail() != null) prefs.setNotifEmail(request.getNotifEmail());
        if (request.getNotifSms() != null) prefs.setNotifSms(request.getNotifSms());
        if (request.getNotifPromociones() != null) prefs.setNotifPromociones(request.getNotifPromociones());
        if (request.getIdioma() != null) prefs.setIdioma(request.getIdioma());
        if (request.getMoneda() != null) prefs.setMoneda(request.getMoneda());

        return mapearPreferencias(preferenciaRepo.save(prefs));
    }

    @Transactional(readOnly = true)
    public PreferenciaDTO.PreferenciaResponse obtenerPreferencias(Long usuarioId) {
        return preferenciaRepo.findByUsuarioId(usuarioId)
            .map(this::mapearPreferencias)
            .orElse(new PreferenciaDTO.PreferenciaResponse());
    }

    // ─── Helpers de búsqueda ──────────────────────────────────────

    @Transactional(readOnly = true)
    public UsuarioDTO.UsuarioResponse buscarPorEmail(String email) {
        Usuario usuario = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new ShopSmartException.UsuarioNoEncontrado(email));
        return mapearUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public Long obtenerIdPorEmail(String email) {
        return usuarioRepo.findByEmail(email)
            .map(Usuario::getId)
            .orElseThrow(() -> new ShopSmartException.UsuarioNoEncontrado(email));
    }

    // ─── Helpers de mapeo ─────────────────────────────────────────

    private UsuarioDTO.AuthResponse generarAuthResponse(Usuario usuario) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String accessToken = jwtUtil.generarToken(userDetails);
        String refreshToken = jwtUtil.generarRefreshToken(userDetails);

        return UsuarioDTO.AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtUtil.getExpiration())
            .usuario(mapearUsuario(usuario))
            .build();
    }

    public UsuarioDTO.UsuarioResponse mapearUsuario(Usuario u) {
        return UsuarioDTO.UsuarioResponse.builder()
            .id(u.getId())
            .nombre(u.getNombre())
            .apellido(u.getApellido())
            .email(u.getEmail())
            .telefono(u.getTelefono())
            .rol(u.getRol().name())
            .activo(u.getActivo())
            .ultimoAcceso(u.getUltimoAcceso())
            .fechaCreacion(u.getFechaCreacion())
            .direcciones(u.getDirecciones() != null
                ? u.getDirecciones().stream().map(this::mapearDireccion).collect(Collectors.toList())
                : List.of())
            .build();
    }

    private DireccionDTO.DireccionResponse mapearDireccion(DireccionEnvio d) {
        return DireccionDTO.DireccionResponse.builder()
            .id(d.getId())
            .calle(d.getCalle())
            .ciudad(d.getCiudad())
            .region(d.getRegion())
            .codigoPostal(d.getCodigoPostal())
            .pais(d.getPais())
            .esPrincipal(d.getEsPrincipal())
            .alias(d.getAlias())
            .build();
    }

    private PreferenciaDTO.PreferenciaResponse mapearPreferencias(PreferenciaUsuario p) {
        List<String> categorias = (p.getCategoriasFavoritas() != null && !p.getCategoriasFavoritas().isBlank())
            ? Arrays.asList(p.getCategoriasFavoritas().split(","))
            : List.of();

        return PreferenciaDTO.PreferenciaResponse.builder()
            .categoriasFavoritas(categorias)
            .notifEmail(p.getNotifEmail())
            .notifSms(p.getNotifSms())
            .notifPromociones(p.getNotifPromociones())
            .idioma(p.getIdioma())
            .moneda(p.getMoneda())
            .build();
    }
}
