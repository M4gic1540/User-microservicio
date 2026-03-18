package com.shopsmart.usuarios.repository;

import com.shopsmart.usuarios.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio JPA para consultas y operaciones sobre usuarios.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Usuario> findByActivoTrue(Pageable pageable);

    Page<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String nombre, String apellido, String email, Pageable pageable
    );

    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoAcceso = :fecha WHERE u.id = :id")
    void actualizarUltimoAcceso(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true")
    long contarUsuariosActivos();
}
