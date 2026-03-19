package com.shopsmart.usuarios.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa a un usuario del sistema.
 */
@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_usuario_email", columnList = "email", unique = true),
    @Index(name = "idx_usuario_activo", columnList = "activo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    // Identificador unico del usuario.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos personales basicos.
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    // Email usado como identificador de acceso.
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Hash de contraseña.
    @Column(nullable = false)
    private String password;

    // Datos de contacto opcionales.
    @Column(length = 20)
    private String telefono;

    // Rol y estado de la cuenta.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Rol rol = Rol.CLIENTE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    // Auditoria de uso y creacion.
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Direcciones asociadas al usuario.
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DireccionEnvio> direcciones = new ArrayList<>();

    // Preferencias de personalizacion.
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PreferenciaUsuario preferencias;

    public enum Rol {
        CLIENTE, ADMIN, SOPORTE
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
