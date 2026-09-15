package com.example.tutoria1.Service.interfaces;

import com.example.tutoria1.Enums.Documento.TipoVehiculo;
import com.example.tutoria1.Model.DocumentoModel;

import java.util.List;

public interface IDocumentoService {
    
    DocumentoModel crearDocumento(DocumentoModel documento);
    DocumentoModel obtenerDocumentoPorId(Long id);
    DocumentoModel actualizarDocumento(Long id, DocumentoModel documento);
    void eliminarDocumento(Long id);
    List<DocumentoModel> listarDocumentos();
    List<DocumentoModel> listarDocumentosPorTipoVehiculo(TipoVehiculo tipoVehiculo);
}
