package com.example.tutoria1.Dto.Vehiculo.Request;

import java.util.List;

import com.example.tutoria1.Dto.VehiculoDocumento.Request.VehiculoDocumentoRequestDto;
import com.example.tutoria1.Enums.Vehiculo.TipoCombustible;
import com.example.tutoria1.Enums.Vehiculo.TipoServicio;
import com.example.tutoria1.Enums.Vehiculo.TipoVehiculo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculoRequestDto {

    private TipoVehiculo tipoVehiculo;
    private TipoServicio tipoServicio;
    private TipoCombustible tipoCombustible;
    private String placa;
    private Integer capacidadPasajeros;
    private String colorHexadecimal;
    private Integer modelo;
    private String marca;
    private String linea;
    private List<VehiculoDocumentoRequestDto> documentos;
}
