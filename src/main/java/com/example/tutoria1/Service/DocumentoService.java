package com.example.tutoria1.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tutoria1.Enums.Documento.Estado;
import com.example.tutoria1.Enums.Documento.TipoVehiculo;
import com.example.tutoria1.Model.DocumentoModel;
import com.example.tutoria1.Service.interfaces.IDocumentoService;
import com.example.tutoria1.repository.DocumentoRepository;

@Service 
public class DocumentoService implements IDocumentoService {
    
    private final DocumentoRepository documentoRepository;

    public DocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public DocumentoModel crearDocumento(DocumentoModel documento) {
        DocumentoModel documentoCreado = documentoRepository.save(documento);
        return documentoCreado;
    }

    public DocumentoModel obtenerDocumentoPorId(Long id) {
        DocumentoModel documentoEncontrado = documentoRepository.findById(id).orElse(null);
        return documentoEncontrado;
    }

    public DocumentoModel actualizarDocumento(Long id, DocumentoModel documento) {
        DocumentoModel documentoExistente = documentoRepository.findById(id).orElse(null);
        if (documentoExistente != null) {
            documentoExistente.setCodigoDocumentoParametrizado(documento.getCodigoDocumentoParametrizado());
            documentoExistente.setNombreDocumento(documento.getNombreDocumento());
            documentoExistente.setTipoVehiculoAlQueAplica(documento.getTipoVehiculoAlQueAplica());
            documentoExistente.setRequisitoSegunTipoVehiculo(documento.getRequisitoSegunTipoVehiculo());
            documentoExistente.setDescripcion(documento.getDescripcion());
            documentoExistente.setEstado(documento.getEstado());
            documentoRepository.save(documentoExistente);
            return documentoExistente;
        }
        return documento;
    }

    public void eliminarDocumento(Long id) {
        documentoRepository.deleteById(id);
    }

    public List<DocumentoModel> listarDocumentos() {
        List<DocumentoModel> documentos = documentoRepository.findAll();
        return documentos;
    }

    public List<DocumentoModel> listarDocumentosPorTipoVehiculo(TipoVehiculo tipoVehiculo) {
        List<DocumentoModel> documentos = documentoRepository.findByTipoVehiculoAlQueAplica(tipoVehiculo);
        return documentos;
    }

    public List<DocumentoModel> listarDocumentosPorEstado(Estado estado) {
        List<DocumentoModel> documentos = documentoRepository.findByEstado(estado);
        return documentos;
    }

    public List<DocumentoModel> listarDocumentosPorPlaca(String placa) {
        List<DocumentoModel> documentos = documentoRepository.findByPlaca(placa);
        return documentos;
    }
}
