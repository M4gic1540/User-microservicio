package com.shopsmart.usuarios.repository;

import com.shopsmart.usuarios.model.PreferenciaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para preferencias de usuario.
 */
@Repository
public interface PreferenciaRepository extends JpaRepository<PreferenciaUsuario, Long> {
    Optional<PreferenciaUsuario> findByUsuarioId(Long usuarioId);
}
