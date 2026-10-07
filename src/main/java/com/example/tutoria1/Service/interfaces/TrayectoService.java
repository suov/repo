package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Dto.Trayecto.Request.TrayectoRequestDto;
import com.example.tutoria1.Dto.Trayecto.Response.TrayectoResponseDto;

public interface TrayectoService {

    TrayectoResponseDto crearTrayecto(
            TrayectoRequestDto request,
            String loginUsuario
    );

    List<TrayectoResponseDto> consultarRutaPorCodigo(
            String codigoRuta
    );
}