package com.example.tutoria1.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    @Override
    public DocumentoModel obtenerDocumentoPorId(Long id) {

        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Documento no encontrado"));
    }

    @Override
    public DocumentoModel actualizarDocumento(Long id, DocumentoModel documento) {

        DocumentoModel documentoExistente = obtenerDocumentoPorId(id);
        documentoExistente.setCodigoDocumentoParametrizado(documento.getCodigoDocumentoParametrizado());
        documentoExistente.setNombreDocumento(documento.getNombreDocumento());
        documentoExistente.setTipoVehiculoAlQueAplica(documento.getTipoVehiculoAlQueAplica());
        documentoExistente.setRequisitoSegunTipoVehiculo(documento.getRequisitoSegunTipoVehiculo());
        documentoExistente.setDescripcion(documento.getDescripcion());
        return documentoRepository.save(documentoExistente);
    }

    @Override
    public void eliminarDocumento(Long id) {
        DocumentoModel documento = obtenerDocumentoPorId(id);
        documentoRepository.delete(documento);
    }

    @Override
    public List<DocumentoModel> listarDocumentos() {

        List<DocumentoModel> documentos = documentoRepository.findAll();
        return documentos;
    }

    @Override
    public List<DocumentoModel> listarDocumentosPorTipoVehiculo(TipoVehiculo tipoVehiculo) {

        List<DocumentoModel> documentos = documentoRepository.findByTipoVehiculoAlQueAplica(tipoVehiculo);
        return documentos;
    }
}
