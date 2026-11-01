package com.example.tutoria1.Controller;

import java.util.List;

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
import org.springframework.web.server.ResponseStatusException;

import com.example.tutoria1.Dto.VehiculoPersona.Request.VehiculoPersonaRequestDto;
import com.example.tutoria1.Dto.VehiculoPersona.Response.VehiculoPersonaResponseDto;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Service.interfaces.IVehiculoPersonaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vehiculo-persona")
@RequiredArgsConstructor
@Tag(
    name = "Vehículo - Persona",
    description = "Operaciones para gestionar las asociaciones entre vehículos y personas"
)
public class VehiculoPersonaController {

    private final IVehiculoPersonaService vehiculoPersonaService;

    @Operation(
        summary = "Crear asociación entre vehículo y persona",
        description = "Registra una asociación entre un vehículo y una persona utilizando los datos proporcionados."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Asociación creada correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VehiculoPersonaResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Los datos enviados para crear la asociación no son válidos",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<VehiculoPersonaResponseDto> crearAsociacion(
            @RequestBody VehiculoPersonaRequestDto requestDto
    ) {

        VehiculoPersonaResponseDto respuesta =
                vehiculoPersonaService.crearAsociacion(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(
        summary = "Listar todas las asociaciones",
        description = "Obtiene todas las asociaciones registradas entre vehículos y personas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Asociaciones obtenidas correctamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation = VehiculoPersonaResponseDto.class)
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<VehiculoPersonaResponseDto>> listarTodos() {
        return ResponseEntity.ok(
                vehiculoPersonaService.listarTodos()
        );
    }

    @Operation(
        summary = "Listar asociaciones por vehículo",
        description = "Obtiene las asociaciones correspondientes al vehículo identificado por su ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Asociaciones del vehículo obtenidas correctamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation = VehiculoPersonaResponseDto.class)
                )
            )
        )
    })
    @GetMapping("/vehiculo/{idVehiculo}")
    public ResponseEntity<List<VehiculoPersonaResponseDto>> listarPorVehiculo(
            @Parameter(
                description = "Identificador del vehículo que se desea consultar",
                example = "1"
            )
            @PathVariable Long idVehiculo
    ) {

        return ResponseEntity.ok(
                vehiculoPersonaService.listarPorVehiculo(idVehiculo)
        );
    }

    @Operation(
        summary = "Listar conductores que pueden operar",
        description = "Obtiene las asociaciones cuyo estado de conductor corresponde a PO (puede operar)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Conductores que pueden operar obtenidos correctamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation = VehiculoPersonaResponseDto.class)
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encuentran conductores que pueden operar",
            content = @Content
        )
    })
    @GetMapping("/conductores/pueden-operar")
    public ResponseEntity<List<VehiculoPersonaResponseDto>> listarConductoresQuePuedenOperar() {

        List<VehiculoPersonaResponseDto> personas = vehiculoPersonaService.listarTodos().stream()
                        .filter(asociacion -> asociacion.getEstadoConductor() == EstadoConductor.PO)
                        .toList();

        if (personas.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encuentran conductores que pueden operar"
                );
        }

        return ResponseEntity.ok(personas);
    }

    @Operation(
        summary = "Actualizar estado de una asociación",
        description = "Actualiza el estado del conductor en una asociación específica entre vehículo y persona."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estado de la asociación actualizado correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VehiculoPersonaResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "El estado enviado no es válido",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró la asociación indicada",
            content = @Content
        )
    })
    @PutMapping("/{idAsociacion}/estado")
    public ResponseEntity<VehiculoPersonaResponseDto> actualizarEstado(
            @Parameter(
                description = "Identificador de la asociación que se actualizará",
                example = "1"
            )
            @PathVariable Long idAsociacion,

            @Parameter(
                description = "Nuevo estado del conductor",
                example = "PO"
            )
            @RequestParam EstadoConductor estado
    ) {

        return ResponseEntity.ok(
                vehiculoPersonaService.actualizarEstado(
                        idAsociacion,
                        estado
                )
        );
    }

    @Operation(
        summary = "Eliminar asociación",
        description = "Elimina la asociación entre vehículo y persona identificada por su ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Asociación eliminada correctamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró la asociación que se desea eliminar",
            content = @Content
        )
    })
    @DeleteMapping("/{idAsociacion}")
    public ResponseEntity<Void> eliminarAsociacion(
            @Parameter(
                description = "Identificador de la asociación que se eliminará",
                example = "1"
            )
            @PathVariable Long idAsociacion
    ) {

        vehiculoPersonaService.eliminarAsociacion(idAsociacion);
        return ResponseEntity.noContent().build();
    }
}