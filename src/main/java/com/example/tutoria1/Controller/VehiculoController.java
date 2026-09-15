package com.example.tutoria1.Controller;

import com.example.tutoria1.Model.VehiculoModel;
import com.example.tutoria1.Service.interfaces.VehiculoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    /* POST */
    @PostMapping
    public ResponseEntity<VehiculoModel> crearVehiculo(
            @RequestBody VehiculoModel vehiculo) {

        VehiculoModel vehiculoCreado = vehiculoService.crearVehiculo(vehiculo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vehiculoCreado);
    }

    /* GET */
    @GetMapping
    public List<VehiculoModel> listarVehiculos() {
        return vehiculoService.listarVehiculos();
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<VehiculoModel> buscarVehiculoPorPlaca(@PathVariable String placa) {
        Optional<VehiculoModel> vehiculo = vehiculoService.consultarVehiculoPorPlaca(placa);
        if (vehiculo.isPresent()) {
            return ResponseEntity.ok(vehiculo.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/tipoVehiculo/{tipoVehiculo}")
    public ResponseEntity<VehiculoModel> buscarVehiculoPorTipoVehiculo(@PathVariable String tipoVehiculo) {
        Optional<VehiculoModel> vehiculo = vehiculoService.consultarVehiculoPorTipVehiculo(tipoVehiculo);
        if (vehiculo.isPresent()) {
            return ResponseEntity.ok(vehiculo.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public VehiculoModel obtenerVehiculoPorId(@PathVariable Long id) {
        return vehiculoService.obtenerVehiculoPorId(id);
    }

    /* PUT */
    @PutMapping("/{id}")
    public VehiculoModel actualizarVehiculo(
            @PathVariable Long id,
            @RequestBody VehiculoModel vehiculo) {

        return vehiculoService.actualizarVehiculo(id, vehiculo);
    }

    /* DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {

        vehiculoService.eliminarVehiculo(id);

        return ResponseEntity.noContent().build();
    }
}