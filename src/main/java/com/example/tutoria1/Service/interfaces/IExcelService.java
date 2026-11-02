package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Dto.Trayecto.Response.CargueResponseDto;

public interface IExcelService {

    List<CargueResponseDto> consultarCargue(Double idCargue);

    boolean cargarDatosExcel(
            String personaId,
            String vehiculoId,
            String codigoRuta,
            String ubicacion,
            String ordenParada,
            String latitud,
            String longitud,
            String loginUsuario,
            Double idCargue);

    boolean validarDatosExcel(Double idCargue);

    boolean procesarDatosExcel(Double idCargue);
}