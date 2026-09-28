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
import com.example.tutoria1.Model.VehiculoModel;
import com.example.tutoria1.Service.interfaces.IVehiculoPersonaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vehiculo-persona")
@RequiredArgsConstructor
public class VehiculoPersonaController {

        private final IVehiculoPersonaService vehiculoPersonaService;

    @PostMapping
    public ResponseEntity<VehiculoPersonaResponseDto> crearAsociacion(
            @RequestBody VehiculoPersonaRequestDto requestDto
    ) {

        VehiculoPersonaResponseDto respuesta =
                vehiculoPersonaService.crearAsociacion(requestDto);

                return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        }

        @GetMapping
        public ResponseEntity<List<VehiculoPersonaResponseDto>> listarTodos() {
                return ResponseEntity.ok(
                                vehiculoPersonaService.listarTodos());
        }

    @GetMapping("/vehiculo/{idVehiculo}")
    public ResponseEntity<List<VehiculoPersonaResponseDto>> listarPorVehiculo(
            @PathVariable Long idVehiculo
    ) {

        return ResponseEntity.ok(
                vehiculoPersonaService.listarPorVehiculo(idVehiculo)
        );
    }

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

    @PutMapping("/{idAsociacion}/estado")
    public ResponseEntity<VehiculoPersonaResponseDto> actualizarEstado(
            @PathVariable Long idAsociacion,
            @RequestParam EstadoConductor estado
    ) {

        return ResponseEntity.ok(
                vehiculoPersonaService.actualizarEstado(
                        idAsociacion,
                        estado
                )
        );
    }

    @DeleteMapping("/{idAsociacion}")
    public ResponseEntity<Void> eliminarAsociacion(
            @PathVariable Long idAsociacion
    ) {

        vehiculoPersonaService.eliminarAsociacion(idAsociacion);
        return ResponseEntity.noContent().build();
    }
}