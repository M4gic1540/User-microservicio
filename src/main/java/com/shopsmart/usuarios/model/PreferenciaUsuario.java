package com.shopsmart.usuarios.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad JPA que almacena preferencias de personalizacion del usuario.
 */
@Entity
@Table(name = "preferencias_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    // Categorías favoritas (almacenadas como CSV)
    @Column(name = "categorias_favoritas", length = 500)
    private String categoriasFavoritas;

    // Notificaciones
    @Column(name = "notif_email")
    @Builder.Default
    private Boolean notifEmail = true;

    @Column(name = "notif_sms")
    @Builder.Default
    private Boolean notifSms = false;

    @Column(name = "notif_promociones")
    @Builder.Default
    private Boolean notifPromociones = true;

    // Idioma y moneda preferida
    @Column(length = 10)
    @Builder.Default
    private String idioma = "es";

    @Column(length = 10)
    @Builder.Default
    private String moneda = "CLP";

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
