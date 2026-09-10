package com.example.tutoria1.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Dto.Documento.Request.DocumentoRequestDto;
import com.example.tutoria1.Dto.Documento.Response.DocumentoResponseDto;
import com.example.tutoria1.Enums.Documento.TipoVehiculo;
import com.example.tutoria1.Model.DocumentoModel;
import com.example.tutoria1.Service.DocumentoService;

@RestController
@RequestMapping("/api/documento")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    /* Crear Documento */
    @PostMapping
    public DocumentoResponseDto crearDocumento(
            @RequestBody DocumentoRequestDto documentoRequest
        ) {
        DocumentoModel documento = convertirAmodel(documentoRequest);
        DocumentoModel documentoCreado = documentoService.crearDocumento(documento);
        return convertirAResponse(documentoCreado);
    }

    /* Obtener Documento por ID */
    @GetMapping("/{id}")
    public DocumentoResponseDto obtenerDocumentoPorId(
            @PathVariable("id") Long id
        ) {
        DocumentoModel documento = documentoService.obtenerDocumentoPorId(id);
        return convertirAResponse(documento);
    }

    /* Actualizar Documento */
    @PutMapping("/{id}")
    public DocumentoResponseDto actualizarDocumento(
            @PathVariable("id") Long id,
            @RequestBody DocumentoRequestDto documentoRequest
        ) {
        DocumentoModel documento = convertirAmodel(documentoRequest);
        DocumentoModel documentoActualizado = documentoService.actualizarDocumento(id, documento);
        return convertirAResponse(documentoActualizado);
    }

    /* Eliminar Documento */
    @DeleteMapping("/{id}")
    public void eliminarDocumento(
            @PathVariable("id") Long id
        ) {
        documentoService.eliminarDocumento(id);
    }

    /* Listar todos los Documentos */
    @GetMapping
    public List<DocumentoResponseDto> listarDocumentos() {
        List<DocumentoModel> documentos = documentoService.listarDocumentos();
        return documentos.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /* Listar documentos por tipo de vehículo */
    @GetMapping("/tipoVehiculo")
    public List<DocumentoResponseDto> listarDocumentosPorTipoVehiculo(
            @RequestParam("tipoVehiculo") TipoVehiculo tipoVehiculo
        ) {
        List<DocumentoModel> documentos = documentoService.listarDocumentosPorTipoVehiculo(tipoVehiculo);
        return documentos.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /* ---------------------------DTOS------------------------------------ */

    /* Convertir RequestDto a DocumentoModel */
    private DocumentoModel convertirAmodel(DocumentoRequestDto dto) {
        DocumentoModel documento = new DocumentoModel();
        documento.setCodigoDocumentoParametrizado(dto.getCodigoDocumentoParametrizado());
        documento.setNombreDocumento(dto.getNombreDocumento());
        documento.setTipoVehiculoAlQueAplica(dto.getTipoVehiculoAlQueAplica());
        documento.setRequisitoSegunTipoVehiculo(dto.getRequisitoSegunTipoVehiculo());
        documento.setDescripcion(dto.getDescripcion());
        return documento;
    }

    /* Convertir DocumentoModel a ResponseDto */
    private DocumentoResponseDto convertirAResponse(DocumentoModel documento) {
        if (documento == null) {
            return null;
        }

        DocumentoResponseDto dto = new DocumentoResponseDto();
        dto.setId(documento.getId());
        dto.setCodigoDocumentoParametrizado(documento.getCodigoDocumentoParametrizado());
        dto.setNombreDocumento(documento.getNombreDocumento());
        dto.setTipoVehiculoAlQueAplica(documento.getTipoVehiculoAlQueAplica());
        dto.setRequisitoSegunTipoVehiculo(documento.getRequisitoSegunTipoVehiculo());
        dto.setDescripcion(documento.getDescripcion());
        return dto;
    }
}