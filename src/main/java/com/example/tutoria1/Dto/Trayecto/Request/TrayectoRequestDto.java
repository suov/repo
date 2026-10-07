package com.example.tutoria1.Dto.Trayecto.Request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrayectoRequestDto {

    private Long conductorId;
    private Long vehiculoId;
    private String codigoRuta;
    private String ubicacion;
    private Integer ordenParada;
    private BigDecimal latitud;
    private BigDecimal longitud;
}