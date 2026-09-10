package com.example.tutoria1.Dto.Documento.Request;

import com.example.tutoria1.Enums.Documento.RequisitoSegunVehiculo;
import com.example.tutoria1.Enums.Documento.TipoVehiculo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentoRequestDto {

    private String codigoDocumentoParametrizado;

    private String nombreDocumento;

    private TipoVehiculo tipoVehiculoAlQueAplica;

    private RequisitoSegunVehiculo requisitoSegunTipoVehiculo;

    private String descripcion;
}