package com.example.tutoria1.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Dto.Exception.ApiResponseDTO;
import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Service.VehiculoDocumentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/vehiculo_documento")
@Tag(
    name = "Vehículo - Documento",
    description = "Operaciones para gestionar las relaciones entre vehículos y documentos"
)
public class VehiculoDocumentoController {

    private final VehiculoDocumentoService vehiculoDocumentoService;

    public VehiculoDocumentoController(VehiculoDocumentoService vehiculoDocumentoService) {
        this.vehiculoDocumentoService = vehiculoDocumentoService;
    }

    @Operation(
        summary = "Crear relación entre vehículo y documento",
        description = "Registra una relación entre un vehículo y un documento, incluyendo la información proporcionada en el cuerpo de la solicitud."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Relación vehículo-documento creada correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Los datos enviados para crear la relación no son válidos",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<Object> crearVehiculoDocumento(
            @RequestBody VehiculoDocumentoModel vehiculoDocumento
    ) {

        VehiculoDocumentoModel vehiculoDocumentoCreado = vehiculoDocumentoService
                .crearVehiculoDocumento(vehiculoDocumento);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    new ApiResponseDTO<>(
                        "Relación vehículo-documento creada correctamente",
                        vehiculoDocumentoCreado
                    )
                );
    }

    @Operation(
        summary = "Listar relaciones vehículo-documento",
        description = "Obtiene todas las relaciones registradas entre vehículos y documentos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Relaciones obtenidas correctamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation = VehiculoDocumentoModel.class)
                )
            )
        )
    })
    @GetMapping
    public List<VehiculoDocumentoModel> listarVehiculoDocumento() {
        return vehiculoDocumentoService.listarVehiculoDocumentos();
    }
}