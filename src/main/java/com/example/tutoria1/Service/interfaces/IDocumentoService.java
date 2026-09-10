package com.example.tutoria1.Service.interfaces;

import com.example.tutoria1.Enums.Documento.TipoVehiculo;
import com.example.tutoria1.Model.DocumentoModel;

import java.util.List;

public interface IDocumentoService {
    
    /* Crear Documento */
    DocumentoModel crearDocumento(DocumentoModel documento);

    /* Obtener Documento por ID */
    DocumentoModel obtenerDocumentoPorId(Long id);

    /* Actualizar Documento */
    DocumentoModel actualizarDocumento(Long id, DocumentoModel documento);

    /* Eliminar Documento */
    void eliminarDocumento(Long id);

    /* Listar todos los Documentos */
    List<DocumentoModel> listarDocumentos();

    /* Listar documentos por tipo de vehículo */
    List<DocumentoModel> listarDocumentosPorTipoVehiculo(TipoVehiculo tipoVehiculo);
}
