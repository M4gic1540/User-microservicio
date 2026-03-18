package com.shopsmart.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que representa una direccion de envio del usuario.
 */
@Entity
@Table(name = "direcciones_envio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DireccionEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 200)
    private String calle;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(nullable = false, length = 20)
    private String codigoPostal;

    @Column(nullable = false, length = 100)
    private String pais;

    @Column(name = "es_principal")
    @Builder.Default
    private Boolean esPrincipal = false;

    @Column(length = 100)
    private String alias;
}
