package com.shopsmart.usuarios.repository;

import com.shopsmart.usuarios.model.DireccionEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para direcciones de envio de usuarios.
 */
@Repository
public interface DireccionRepository extends JpaRepository<DireccionEnvio, Long> {

    List<DireccionEnvio> findByUsuarioId(Long usuarioId);

    Optional<DireccionEnvio> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<DireccionEnvio> findByUsuarioIdAndEsPrincipalTrue(Long usuarioId);

    @Modifying
    @Query("UPDATE DireccionEnvio d SET d.esPrincipal = false WHERE d.usuario.id = :usuarioId")
    void desmarcarTodoPrincipal(@Param("usuarioId") Long usuarioId);
}
