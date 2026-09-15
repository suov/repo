package com.example.tutoria1.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
///import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Service.VehiculoDocumentoService;

@RestController
@RequestMapping("/api/vehiculo_documento")
public class VehiculoDocumentoController {

    private final VehiculoDocumentoService vehiculoDocumentoService;

    public VehiculoDocumentoController(VehiculoDocumentoService vehiculoDocumentoService) {
        this.vehiculoDocumentoService = vehiculoDocumentoService;
    }

    @PostMapping
    public ResponseEntity<VehiculoDocumentoModel> crearVehiculoDocumento(
            @RequestBody VehiculoDocumentoModel vehiculoDocumento) {

        VehiculoDocumentoModel vehiculoDocumentoCreado = vehiculoDocumentoService
                .crearVehiculoDocumento(vehiculoDocumento);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vehiculoDocumentoCreado);
    }

    @GetMapping
    public List<VehiculoDocumentoModel> listarVehiculoDocumento() {
        return vehiculoDocumentoService.listarVehiculoDocumentos();
    }
    /*
     * Función de encontrar por nombre- no funciona
     * 
     * @GetMapping("/nombreDocumento/{nombreDocumento}")
     * public List<VehiculoDocumentoModel>
     * buscarVehiculoPorNombreDocumento(@PathVariable String nombreDocumento) {
     * return
     * vehiculoDocumentoService.buscarVehoculosPorNombreDocumento(nombreDocumento);
     * }
     */

}
