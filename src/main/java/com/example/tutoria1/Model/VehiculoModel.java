package com.example.tutoria1.Model;

import java.util.List;

import com.example.tutoria1.Enums.Vehiculo.TipoCombustible;
import com.example.tutoria1.Enums.Vehiculo.TipoServicio;
import com.example.tutoria1.Enums.Vehiculo.TipoVehiculo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehiculos", check = {
                /* Condiciones de los atributos de vehiculo */
                @CheckConstraint(name = "chk_tipo_vehiculo", constraint = "tipo_vehiculo IN ('AUTOMOVIL', 'MOTOCICLETA')"),
                @CheckConstraint(name = "chk_placa", constraint = "(tipo_vehiculo = 'AUTOMOVIL' AND placa REGEXP '^[A-Za-z]{3}[0-9]{3}$')"
                                +
                                "OR (tipo_vehiculo = 'MOTOCICLETA' AND placa REGEXP '^[A-Za-z]{3}[0-9]{2}[A-Za-z]$')"),
                @CheckConstraint(name = "chk_cap_pasajeros", constraint = "(capacidad_pasajeros >= 0)"),
                @CheckConstraint(name = "chk_modelo", constraint = "(modelo >= 0)")
})
public class VehiculoModel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_vehiculo", nullable = false)
        private TipoVehiculo tipoVehiculo;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_servicio", nullable = false)
        private TipoServicio tipoServicio;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_combustible", nullable = false)
        private TipoCombustible tipoCombustible;

        @Column(name = "placa", nullable = false, unique = true, length = 6)
        private String placa;

        @Column(name = "capacidad_pasajeros", nullable = false)
        private Integer capacidadPasajeros;

        @Column(name = "color", nullable = false)
        private String colorHexadecimal;

        @Column(name = "modelo", nullable = false)
        private Integer modelo;

        @Column(name = "marca", nullable = false)
        private String marca;

        @Column(name = "linea", nullable = false)
        private String linea;

        @JsonIgnore
        @OneToMany(mappedBy = "vehiculo")
        private List<VehiculoDocumentoModel> documentos;
}