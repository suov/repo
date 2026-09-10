package com.example.tutoria1.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Enums.Documento.Estado;
import com.example.tutoria1.Enums.Documento.TipoVehiculo;
import com.example.tutoria1.Model.DocumentoModel;
import com.example.tutoria1.Service.DocumentoService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
@RequestMapping ("/api/documento")
public class DocumentoController {
    
    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @PostMapping
    public DocumentoModel crearDocumento(
        @RequestBody DocumentoModel documento
    ) {
        return documentoService.crearDocumento(documento);
    }
    
    @GetMapping("/{id}")
    public DocumentoModel obtenerDocumentoPorId(
        @PathVariable ("id") Long id
    ) {
        return documentoService.obtenerDocumentoPorId(id);
    }

    @PutMapping("/{id}")
    public DocumentoModel actualizarDocumento(
        @PathVariable ("id") Long id,
        @RequestBody DocumentoModel documento
    ) {
        return documentoService.actualizarDocumento(id, documento);
    }
    
    @DeleteMapping("/{id}")
    public void eliminarDocumento(
        @PathVariable ("id") Long id
    ) {
        documentoService.eliminarDocumento(id);
    }

    @GetMapping
    public List<DocumentoModel> listarDocumentos() {
        return documentoService.listarDocumentos();
    }

    @GetMapping("/tipoVehiculo")
    public List<DocumentoModel> listarDocumentosPorTipoVehiculo(
        @RequestParam ("tipoVehiculo") TipoVehiculo tipoVehiculo
    ) {
        return documentoService.listarDocumentosPorTipoVehiculo(tipoVehiculo);
    }

    @GetMapping("/estado")
    public List<DocumentoModel> listarDocumentosPorEstado(
        @RequestParam ("estado") Estado estado
    ) {
        return documentoService.listarDocumentosPorEstado(estado);
    }
    
    @GetMapping("/placa")
    public List<DocumentoModel> listarDocumentosPorPlaca(
        @RequestParam ("placa") String placa
    ) {
        return documentoService.listarDocumentosPorPlaca(placa);
    }
}
