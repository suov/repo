package com.example.tutoria1.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trayecto", uniqueConstraints = {
        @UniqueConstraint(name = "uk_trayecto", columnNames = { "id_persona", "id_vehiculo" })
})
public class TrayectoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private PersonaModel persona;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private VehiculoModel vehiculo;

    @Column(name = "cod_ruta", nullable = false)
    private String CodRuta;

    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

    @Column(name = "orden_parada", nullable = false)
    private Integer ordenParada;

    @Column(name = "longitud", nullable = false)
    private double longitud;

    @Column(name = "latitud", nullable = false)
    private double latitud;

    @Column(name = "login", nullable = false)
    private String login;

}
