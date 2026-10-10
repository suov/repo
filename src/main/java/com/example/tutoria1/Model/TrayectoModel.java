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

    public void validarConductor(PersonaModel conductor) {

        if (conductor == null || conductor.getTipoPersona() != TipoPersona.C) {

            throw new IllegalArgumentException(
                    "ERROR: La persona asignada debe ser un conductor."
            );
        }
    }

    public void validarCodigoRuta(String codigoRuta) {

        if (codigoRuta == null || codigoRuta.isBlank()) {

            throw new IllegalArgumentException(
                    "ERROR: El código de ruta es obligatorio."
            );
        }
    }

    public void validarDocumentos(VehiculoModel vehiculo) {

        if (vehiculo == null || vehiculo.getDocumentos() == null
                || vehiculo.getDocumentos().isEmpty()) {

            throw new IllegalArgumentException(
                    "ERROR: El vehículo asociado no tiene documentos registrados."
            );
        }

        for (VehiculoDocumentoModel documento : vehiculo.getDocumentos()) {
            if (documento == null
                    || documento.getEstadoDocumento() != VehiculoDocumentoStatus.HABILITADO) {
                throw new IllegalArgumentException(
                        "ERROR: El vehiculo: " + vehiculo.getPlaca()
                                + ", no puede operar debido a un documento no habilitado."
                );
            }
        }
    }

    public void validarVehiculo(VehiculoModel vehiculo, PersonaModel persona) {

        List<VehiculoPersonaModel> conductores = vehiculo.getConductores();

        if (conductores == null || conductores.isEmpty()) {
            throw new IllegalArgumentException(
                    "ERROR: El vehiculo asociado no contiene conductores."
            );
        }

        for (VehiculoPersonaModel conductor : conductores) {
            if (conductor.getId().equals(persona.getId())) {
                if (conductor.getEstadoConductor() != EstadoConductor.PO) {
                    throw new IllegalArgumentException(
                            "ERROR: La relación del Vehiculo: "
                                    + vehiculo.getPlaca() + ", con el conductor: "
                                    + persona.getNombre() + ", no esta disponible para operar."
                    );
                }
            }
        }
    }
}
