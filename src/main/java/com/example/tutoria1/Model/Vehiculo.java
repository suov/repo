package com.example.tutoria1.Model.Vehiculo;

import com.example.tutoria1.Enums.Vehiculo.TipoCombustible;
import com.example.tutoria1.Enums.Vehiculo.TipoServicio;
import com.example.tutoria1.Enums.Vehiculo.TipoVehiculo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoVehiculo tipo;

    @Enumerated(EnumType.STRING)
    private TipoServicio tipoServicio;

    @Enumerated(EnumType.STRING)
    private TipoCombustible tipoCombustible;

    @Column(nullable = false, unique = true, length = 6)
    private String placa;

    @Column(nullable = false)
    private Integer capacidadPasajeros;

    @Column(nullable = false)
    private String colorHexadecimal;

    @Column(nullable = false)
    private Integer modelo;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String linea;

    public Long getId() {
        return id;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public TipoCombustible getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(TipoCombustible tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Integer getCapacidadPasajeros() {
        return capacidadPasajeros;
    }

    public void setCapacidadPasajeros(Integer capacidadPasajeros) {
        this.capacidadPasajeros = capacidadPasajeros;
    }

    public String getColorHexadecimal() {
        return colorHexadecimal;
    }

    public void setColorHexadecimal(String colorHexadecimal) {
        this.colorHexadecimal = colorHexadecimal;
    }

    public Integer getModelo() {
        return modelo;
    }

    public void setModelo(Integer modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    
}