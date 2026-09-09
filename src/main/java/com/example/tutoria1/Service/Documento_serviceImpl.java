package com.example.tutoria1.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tutoria1.Model.Documento;
import com.example.tutoria1.Model.enums.Documento_Estado;
import com.example.tutoria1.Model.enums.Documento_TipoVehiculo;
import com.example.tutoria1.Service.repository.Documento_repository;

@Service 
public class Documento_serviceImpl extends Documento_service {
    
    private final Documento_repository documentoRepository;

    public Documento_serviceImpl(Documento_repository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public Documento crearDocumento(Documento documento) {
        Documento documentoCreado = documentoRepository.save(documento);
        return documentoCreado;
    }

    public Documento obtenerDocumentoPorId(Long id) {
        Documento documentoEncontrado = documentoRepository.findById(id).orElse(null);
        return documentoEncontrado;
    }

    public Documento actualizarDocumento(Long id, Documento documento) {
        Documento documentoExistente = documentoRepository.findById(id).orElse(null);
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
        // Lógica para eliminar un documento
    }

    public List<Documento> listarDocumentos() {
        // Lógica para listar todos los documentos
        return null;
    }

    public List<Documento> listarDocumentosPorTipoVehiculo(Documento_TipoVehiculo tipoVehiculo) {
        // Lógica para listar documentos por tipo de vehículo
        return null;
    }

    public List<Documento> listarDocumentosPorEstado(Documento_Estado estado) {
        // Lógica para listar documentos por estado
        return null;
    }

    public List<Documento> listarDocumentosPorPlaca(String placa) {
        // Lógica para listar documentos por placa
        return null;
    }
}
