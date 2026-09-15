package com.example.tutoria1.Dto.Vehiculo.Response;

import java.util.List;

import com.example.tutoria1.Dto.VehiculoDocumento.Response.VehiculoDocumentoResponseDto;
import com.example.tutoria1.Enums.Vehiculo.TipoCombustible;
import com.example.tutoria1.Enums.Vehiculo.TipoServicio;
import com.example.tutoria1.Enums.Vehiculo.TipoVehiculo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculoResponseDto {

    private Long id;
    private TipoVehiculo tipoVehiculo;
    private TipoServicio tipoServicio;
    private TipoCombustible tipoCombustible;
    private String placa;
    private Integer capacidadPasajeros;
    private String colorHexadecimal;
    private Integer modelo;
    private String marca;
    private String linea;
    private List<VehiculoDocumentoResponseDto> documentos;
}
