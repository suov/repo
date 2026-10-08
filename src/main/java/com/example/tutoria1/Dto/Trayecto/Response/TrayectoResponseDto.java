package com.example.tutoria1.Dto.Trayecto.Response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrayectoResponseDto {

    private Long id;
    private Long conductorId;
    private String nombreConductor;
    private Long vehiculoId;
    private String placaVehiculo;
    private String codigoRuta;
    private String ubicacion;
    private Integer ordenParada;
    private BigDecimal latitud;
    private BigDecimal longitud;
}