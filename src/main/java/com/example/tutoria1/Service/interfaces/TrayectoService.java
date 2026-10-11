package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Dto.Trayecto.Request.TrayectoRequestDto;
import com.example.tutoria1.Dto.Trayecto.Response.TrayectoResponseDto;
import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;

public interface TrayectoService {

        TrayectoResponseDto crearTrayecto(
                        TrayectoRequestDto request,
                        String loginUsuario);

        List<TrayectoResponseDto> consultarRutaPorCodigo(
                        String codigoRuta);

        List<TrayectoResponseDto> consultarRutaInformacionPorVehiculoYConductor(VehiculoDocumentoStatus statusVehiculo,
                        EstadoConductor estadoConductor);
}