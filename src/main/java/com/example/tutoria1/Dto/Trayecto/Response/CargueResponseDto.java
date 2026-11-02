package com.example.tutoria1.Dto.Trayecto.Response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CargueResponseDto {

    private Long id;
    private String personaId;
    private String vehiculoId;
    private String codigoRuta;
    private String ubicacion;
    private Integer ordenParada;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String loginUsuario;
    private String estado;
    private String observacion;
    private Double idCargue;
}
