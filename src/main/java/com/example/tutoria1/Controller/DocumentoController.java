package com.example.tutoria1.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.example.tutoria1.Dto.Exception.ApiResponseDTO;
import com.example.tutoria1.Enums.Documento.TipoVehiculo;
import com.example.tutoria1.Model.DocumentoModel;
import com.example.tutoria1.Service.DocumentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/documento")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @Operation(
        summary = "Registrar un documento",
        description = "Permite crear un nuevo documento parametrizado, registrando su código, nombre, tipo de vehículo al que aplica, requisito y descripción."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Documento creado correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Los datos enviados no son válidos", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al registrar el documento", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Object> crearDocumento(
            @RequestBody DocumentoRequestDto documentoRequest
    ) {

        DocumentoModel documento = convertirAmodel(documentoRequest);
        DocumentoModel documentoCreado = documentoService.crearDocumento(documento);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                    new ApiResponseDTO<>(
                        "Documento creado correctamente",
                        convertirAResponse(documentoCreado))
                );
    }

    @Operation(
        summary = "Consultar un documento por ID",
        description = "Obtiene la información de un documento parametrizado mediante su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documento encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DocumentoResponseDto.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "No se encontró un documento con el ID indicado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al consultar el documento", content = @Content)
    })
    @GetMapping("/{id}")
    public DocumentoResponseDto obtenerDocumentoPorId(
            @Parameter(description = "Identificador único del documento", example = "1")
            @PathVariable("id") Long id
    ) {

        DocumentoModel documento = documentoService.obtenerDocumentoPorId(id);
        return convertirAResponse(documento);
    }

    @Operation(
        summary = "Actualizar un documento",
        description = "Permite modificar la información de un documento parametrizado existente, identificado mediante su ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documento actualizado correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DocumentoResponseDto.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Los datos enviados no son válidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "No se encontró el documento que se desea actualizar", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al actualizar el documento", content = @Content)
    })
    @PutMapping("/{id}")
    public DocumentoResponseDto actualizarDocumento(
            @Parameter(description = "Identificador único del documento que se desea actualizar", example = "1")
            @PathVariable("id") Long id,
            @RequestBody DocumentoRequestDto documentoRequest
    ) {

        DocumentoModel documento = convertirAmodel(documentoRequest);
        DocumentoModel documentoActualizado = documentoService.actualizarDocumento(id, documento);
        return convertirAResponse(documentoActualizado);
    }

    @Operation(
        summary = "Eliminar un documento",
        description = "Elimina un documento parametrizado mediante su identificador. La respuesta incluye los datos del documento eliminado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documento eliminado correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "No se encontró el documento que se desea eliminar", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al eliminar el documento", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarDocumento(
            @Parameter(description = "Identificador único del documento que se desea eliminar", example = "1")
            @PathVariable("id") Long id
    ) {

        DocumentoModel documentoEliminado = documentoService.obtenerDocumentoPorId(id);
        documentoService.eliminarDocumento(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                    new ApiResponseDTO<>(
                        "Documento eliminado correctamente",
                        convertirAResponse(documentoEliminado))
                );
    }

    @Operation(
        summary = "Listar todos los documentos",
        description = "Obtiene el listado completo de documentos parametrizados registrados en el sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Listado de documentos obtenido correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DocumentoResponseDto.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno al consultar los documentos", content = @Content)
    })
    @GetMapping
    public List<DocumentoResponseDto> listarDocumentos() {

        List<DocumentoModel> documentos = documentoService.listarDocumentos();

        /* Retornamos un secuencial para recorrer */
        return documentos.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Operation(
        summary = "Listar documentos por tipo de vehículo",
        description = "Filtra los documentos parametrizados según el tipo de vehículo al que aplican. Permite consultar los requisitos correspondientes a una categoría específica de vehículo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documentos filtrados correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DocumentoResponseDto.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "El tipo de vehículo enviado no es válido", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al filtrar los documentos", content = @Content)
    })
    @GetMapping("/tipoVehiculo")
    public List<DocumentoResponseDto> listarDocumentosPorTipoVehiculo(
            @Parameter(
                description = "Tipo de vehículo para filtrar los documentos. Debe corresponder a un valor del enum TipoVehiculo.",
                example = "M"
            )
            @RequestParam("tipoVehiculo") TipoVehiculo tipoVehiculo
    ) {

        List<DocumentoModel> documentos = documentoService.listarDocumentosPorTipoVehiculo(tipoVehiculo);

        return documentos.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /* ---------------------------CONSTRUCTORES DE DTOS------------------------------------ */

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