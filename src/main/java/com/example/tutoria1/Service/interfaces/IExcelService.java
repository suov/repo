package com.example.tutoria1.Service.interfaces;

public interface IExcelService {

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