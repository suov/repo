package com.example.tutoria1.Controller;

import com.example.tutoria1.Model.VehiculoModel;
import com.example.tutoria1.Service.VehiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @PostMapping
    public ResponseEntity<VehiculoModel> crearVehiculo(
            @RequestBody VehiculoModel vehiculo) {

        VehiculoModel vehiculoCreado = vehiculoService.crearVehiculo(vehiculo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vehiculoCreado);
    }

    @GetMapping
    public List<VehiculoModel> listarVehiculos() {
        return vehiculoService.listarVehiculos();
    }

    @GetMapping("/{id}")
    public VehiculoModel obtenerVehiculoPorId(@PathVariable Long id) {
        return vehiculoService.obtenerVehiculoPorId(id);
    }

    @PutMapping("/{id}")
    public VehiculoModel actualizarVehiculo(
            @PathVariable Long id,
            @RequestBody VehiculoModel vehiculo) {

        return vehiculoService.actualizarVehiculo(id, vehiculo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {

        vehiculoService.eliminarVehiculo(id);

        return ResponseEntity.noContent().build();
    }
}