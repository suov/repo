package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Model.VehiculoModel;

public interface IVehiculoDocumentoService {

    VehiculoDocumentoModel crearVehiculoDocumento(VehiculoDocumentoModel vehiculoDocumento);

    List<VehiculoDocumentoModel> listarVehiculoDocumentos();

    List<VehiculoModel> buscarVehiculoPorEstadoDocumento(VehiculoDocumentoStatus estado);
}
