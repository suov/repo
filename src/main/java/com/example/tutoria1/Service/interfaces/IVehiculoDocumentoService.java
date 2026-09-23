package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Model.VehiculoDocumentoModel;

public interface IVehiculoDocumentoService {

    VehiculoDocumentoModel crearVehiculoDocumento(VehiculoDocumentoModel vehiculoDocumento);

    List<VehiculoDocumentoModel> listarVehiculoDocumentos();

    /// Funcion no funciona List<VehiculoDocumentoModel>
    /// buscarVehoculosPorNombreDocumento(String nombreDocumento);
}
