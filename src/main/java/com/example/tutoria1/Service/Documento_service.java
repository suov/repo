package com.example.tutoria1.Service;

import com.example.tutoria1.Model.Documento;
import com.example.tutoria1.Model.enums.Documento_Estado;
import com.example.tutoria1.Model.enums.Documento_TipoVehiculo;

import java.util.List;

public interface Documento_service {
    
    /* Crear Documento */
    Documento crearDocumento(Documento documento);

    /* Obtener Documento por ID */
    Documento obtenerDocumentoPorId(Long id);

    /* Actualizar Documento */
    Documento actualizarDocumento(Long id, Documento documento);

    /* Eliminar Documento */
    void eliminarDocumento(Long id);

    /* Listar todos los Documentos */
    List<Documento> listarDocumentos();

    /* Listar documentos por tipo de vehículo */
    List<Documento> listarDocumentosPorTipoVehiculo(Documento_TipoVehiculo tipoVehiculo);

    /* Listar documentos por estado */
    List<Documento> listarDocumentosPorEstado(Documento_Estado estado);

    /* Listar documentos por placa */
    List<Documento> listarDocumentosPorPlaca(String placa);
}
