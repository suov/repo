package com.example.tutoria1.Model;

import java.math.BigDecimal;
import java.util.List;

import com.example.tutoria1.Enums.Persona.TipoPersona;
import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "trayectos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_trayecto_codigo_ruta_orden",
                        columnNames = { "codigo_ruta", "orden_parada" }
                )
        },
        indexes = {
                @Index(name = "idx_trayecto_codigo_ruta", columnList = "codigo_ruta")
        },
        check = {
                @CheckConstraint(
                        name = "chk_orden_parada",
                        constraint = "orden_parada >= 0"
                )
        }
)
public class TrayectoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "persona_id", referencedColumnName = "id", nullable = false)
    private PersonaModel conductor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehiculo_id", referencedColumnName = "id", nullable = false)
    private VehiculoModel vehiculo;

    @Column(name = "codigo_ruta", nullable = false, length = 50)
    private String codigoRuta;

    @Column(name = "ubicacion", nullable = false, length = 255)
    private String ubicacion;

    @Column(name = "orden_parada", nullable = false)
    private Integer ordenParada;

    @Column(name = "latitud", precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(name = "login_usuario", nullable = false, length = 100)
    private String loginUsuario;
}
