package com.example.tutoria1.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/trayectos")
public class TrayectoController {

        private final TrayectoService trayectoService;

        public TrayectoController(TrayectoService trayectoService) {
                this.trayectoService = trayectoService;
        }

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

        @GetMapping({ "/ruta", "/ruta/{codigoRuta}" })
        public ResponseEntity<List<TrayectoResponseDto>> consultarRuta(
                        @PathVariable(required = false) String codigoRuta) {
                /* Validaciones ya en el service */

                return ResponseEntity.ok(
                                trayectoService.consultarRutaPorCodigo(codigoRuta));
        }

        // Consulta de rutas e información de los trayectos: vehiculo NO habilitado y
        // conductor RO
        @GetMapping("/trayectoPorConductorYVehiculo")
        public ResponseEntity<List<TrayectoResponseDto>> consultarRutaPorVehiculoYConductor() {

                return ResponseEntity.ok(trayectoService.consultarRutaPorVehiculoYConductor());
        }

        @GetMapping("/conductor/{documentoConductor}")
        public List<CodigosResponseDto> consultarCodigosDeRutaPorDocumentoCondcutor(
                        @PathVariable String documentoConductor) {

                return trayectoService.consultarCodigosDeRutaPorConductor(documentoConductor);
        }

        @GetMapping("/CodigoRutaYConductorPorPlaca/{placa}")
        public List<RutaConductorResponseDto> consultaCodigoRutaYConductorPorPalca(@PathVariable String placa) {
                return trayectoService.consultaCodigoRutaYConductorPorPalca(placa);
        }

}