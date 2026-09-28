package com.example.tutoria1.Model;

import java.time.LocalDate;

import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "vehiculo_persona",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_vehiculo_persona",
            columnNames = {"id_vehiculo", "id_persona"}
        )
    }
)
public class VehiculoPersonaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private VehiculoModel vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private PersonaModel persona;

    @Column(name = "fecha_asociacion", nullable = false)
    private LocalDate fechaAsociacion;

    /* PO - Puede Operar
    ** EA - Espera de Aprobación
    ** RO - Restringido para Operar */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_conductor", nullable = false, length = 2)
    private EstadoConductor estadoConductor;
}