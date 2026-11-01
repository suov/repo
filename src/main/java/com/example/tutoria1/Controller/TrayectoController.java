package com.example.tutoria1.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Dto.Exception.ApiResponseDTO;
import com.example.tutoria1.Dto.Trayecto.Request.TrayectoRequestDto;
import com.example.tutoria1.Dto.Trayecto.Response.CodigosResponseDto;
import com.example.tutoria1.Dto.Trayecto.Response.RutaConductorResponseDto;
import com.example.tutoria1.Dto.Trayecto.Response.TrayectoResponseDto;
import com.example.tutoria1.Service.interfaces.TrayectoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://127.0.0.1:5500"
})
@RestController
@RequestMapping("/api/trayectos")
public class TrayectoController {

        private final TrayectoService trayectoService;

        public TrayectoController(TrayectoService trayectoService) {
                this.trayectoService = trayectoService;
        }

        @Operation(
                summary = "Registrar un trayecto",
                description = "Registra un nuevo trayecto asociado a un conductor y un vehículo. La operación utiliza la identidad del usuario autenticado para identificar quién realiza el registro. Se almacena el código de ruta, la ubicación y el orden de la parada."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Trayecto registrado correctamente",
                        content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseDTO.class)
                        )
                ),
                @ApiResponse(responseCode = "400", description = "Los datos enviados no son válidos o no cumplen las condiciones requeridas", content = @Content),
                @ApiResponse(responseCode = "401", description = "El usuario no está autenticado o su sesión no es válida", content = @Content),
                @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para registrar trayectos", content = @Content),
                @ApiResponse(responseCode = "404", description = "No se encontró el conductor o vehículo asociado al trayecto", content = @Content),
                @ApiResponse(responseCode = "500", description = "Error interno al registrar el trayecto", content = @Content)
        })
        @PostMapping
        public ResponseEntity<ApiResponseDTO<TrayectoResponseDto>> crearTrayecto(
                @RequestBody TrayectoRequestDto request,
                Authentication authentication) {

                String loginUsuario = authentication.getName();

                TrayectoResponseDto respuesta = trayectoService.crearTrayecto(
                                request,
                                loginUsuario);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(
                                                new ApiResponseDTO<>(
                                                                "Trayecto creado correctamente",
                                                                respuesta));
        }

        @Operation(
                summary = "Consultar trayectos por código de ruta",
                description = "Obtiene los trayectos asociados a un código de ruta específico. Si no se proporciona el código, el servicio realiza la consulta según sus validaciones y comportamiento definidos."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Consulta realizada correctamente",
                        content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = TrayectoResponseDto.class)
                        )
                ),
                @ApiResponse(responseCode = "400", description = "El código de ruta proporcionado no es válido", content = @Content),
                @ApiResponse(responseCode = "404", description = "No se encontraron trayectos para la ruta consultada", content = @Content),
                @ApiResponse(responseCode = "500", description = "Error interno al consultar los trayectos", content = @Content)
        })
        @GetMapping({ "/ruta", "/ruta/{codigoRuta}" })
        public ResponseEntity<List<TrayectoResponseDto>> consultarRuta(
                @Parameter(
                description = "Código que identifica la ruta que se desea consultar",
                example = "RUTA-UT",
                required = false
                )
                @PathVariable(required = false) String codigoRuta) {
                /* Validaciones ya en el service */

                return ResponseEntity.ok(
                                trayectoService.consultarRutaPorCodigo(codigoRuta));
        }

        // Consulta de rutas e información de los trayectos: vehiculo NO habilitado y
        // conductor RO
        @Operation(
                summary = "Consultar trayectos por conductor y vehículo",
                description = "Obtiene información de las rutas y sus trayectos relacionados con vehículos no habilitados y conductores con el estado correspondiente definido por las reglas del servicio."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Información de rutas y trayectos obtenida correctamente",
                        content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = TrayectoResponseDto.class)
                        )
                ),
                @ApiResponse(responseCode = "404", description = "No se encontraron registros relacionados con conductores y vehículos", content = @Content),
                @ApiResponse(responseCode = "500", description = "Error interno al consultar la información", content = @Content)
        })
        @GetMapping("/trayectoPorConductorYVehiculo")
        public ResponseEntity<List<TrayectoResponseDto>> consultarRutaPorVehiculoYConductor() {

                return ResponseEntity.ok(trayectoService.consultarRutaPorVehiculoYConductor());
        }

        @Operation(
                summary = "Consultar códigos de ruta por documento del conductor",
                description = "Obtiene los códigos de las rutas asociadas a un conductor, utilizando su número de documento como criterio de búsqueda."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Códigos de ruta consultados correctamente",
                        content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CodigosResponseDto.class)
                        )
                ),
                @ApiResponse(responseCode = "400", description = "El documento del conductor no es válido", content = @Content),
                @ApiResponse(responseCode = "404", description = "No se encontraron rutas asociadas al conductor", content = @Content),
                @ApiResponse(responseCode = "500", description = "Error interno al consultar los códigos de ruta", content = @Content)
        })
        @GetMapping("/conductor/{documentoConductor}")
        public List<CodigosResponseDto> consultarCodigosDeRutaPorDocumentoCondcutor(
                @Parameter(
                description = "Número de documento del conductor",
                example = "1234567890"
                )
                @PathVariable String documentoConductor) {

                return trayectoService.consultarCodigosDeRutaPorConductor(documentoConductor);
        }

        @Operation(
                summary = "Consultar ruta y conductor por placa",
                description = "Consulta la información de los códigos de ruta y los conductores relacionados con un vehículo, utilizando su placa como criterio de búsqueda."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Información de rutas y conductores obtenida correctamente",
                        content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = RutaConductorResponseDto.class)
                        )
                ),
                @ApiResponse(responseCode = "400", description = "La placa proporcionada no es válida", content = @Content),
                @ApiResponse(responseCode = "404", description = "No se encontraron rutas o conductores asociados al vehículo", content = @Content),
                @ApiResponse(responseCode = "500", description = "Error interno al consultar la información", content = @Content)
        })
        @GetMapping("/CodigoRutaYConductorPorPlaca/{placa}")
        public List<RutaConductorResponseDto> consultaCodigoRutaYConductorPorPlaca(
                @Parameter(
                description = "Placa del vehículo que se desea consultar",
                example = "ABC123"
                )
                @PathVariable String placa) {
                        
                return trayectoService.consultaCodigoRutaYConductorPorPlaca(placa);
        }

}