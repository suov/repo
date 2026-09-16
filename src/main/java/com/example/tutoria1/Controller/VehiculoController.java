package com.example.tutoria1.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.tutoria1.Dto.VehiculoDocumento.Request.VehiculoDocumentoRequestDto;
import com.example.tutoria1.Dto.Vehiculo.Request.VehiculoRequestDto;
import com.example.tutoria1.Dto.VehiculoDocumento.Response.VehiculoDocumentoResponseDto;
import com.example.tutoria1.Dto.Vehiculo.Response.VehiculoResponseDto;
import com.example.tutoria1.Model.DocumentoModel;
import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Model.VehiculoModel;
import com.example.tutoria1.Service.interfaces.VehiculoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @PostMapping
    public ResponseEntity<VehiculoResponseDto> crearVehiculo(@RequestBody VehiculoRequestDto vehiculoRequest) {

        VehiculoModel vehiculoCreado = vehiculoService.crearVehiculo(convertirAModelo(vehiculoRequest));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirAResponse(vehiculoCreado));
    }

    @GetMapping
    public List<VehiculoResponseDto> listarVehiculos() {
        return vehiculoService.listarVehiculos().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /* Filtros de busqueda ---------------- */

    @GetMapping("/{placa}")
    public ResponseEntity<VehiculoResponseDto> buscarVehiculoPorPlaca(@PathVariable String placa) {

        Optional<VehiculoModel> vehiculo = vehiculoService.consultarVehiculoPorPlaca(placa);
        if (vehiculo.isPresent()) {
            return ResponseEntity.ok(convertirAResponse(vehiculo.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{tipoVehiculo}")
    public List<VehiculoResponseDto> buscarVehiculosPorTipoVehiculo(@PathVariable String tipoVehiculo) {
        return vehiculoService.consultarVehiculosPorTipoVehiculo(tipoVehiculo).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/tipoDocumento/{codigoDocumento}")
    public List<VehiculoResponseDto> buscarVehiculosPorTipoDocumento(
            @PathVariable String codigoDocumento) {
        return vehiculoService.consultarVehiculosPorTipoDocumento(codigoDocumento).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/{vehiculoId}/documentos")
    public ResponseEntity<VehiculoDocumentoResponseDto> agregarDocumento(
            @PathVariable Long vehiculoId,
            @RequestBody VehiculoDocumentoRequestDto documentoRequest) {

        VehiculoDocumentoModel documento = vehiculoService.agregarDocumento(
                vehiculoId,
                convertirDocumentoAModelo(documentoRequest));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirDocumentoAResponse(documento));
    }

    @GetMapping("/estadoDocumento/{estadoDocumento}")
    public List<VehiculoResponseDto> buscarVehiculosPorEstadoDocumento(@PathVariable String estadoDocumento){

        return vehiculoService.consultarVehiculosPorEstadoDocumento(estadoDocumento)
            .stream()
            .map(this::convertirAResponse)
            .collect(Collectors.toList());
    }
    
    /* ----------------------- */

    @GetMapping("/{id}")
    public VehiculoResponseDto obtenerVehiculoPorId(@PathVariable Long id) {
        return convertirAResponse(vehiculoService.obtenerVehiculoPorId(id));
    }

    @PutMapping("/{id}")
    public VehiculoResponseDto actualizarVehiculo(
            @PathVariable Long id,
            @RequestBody VehiculoRequestDto vehiculoRequest) {

        return convertirAResponse(
                vehiculoService.actualizarVehiculo(id, convertirAModelo(vehiculoRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {

        vehiculoService.eliminarVehiculo(id);
        return ResponseEntity.noContent().build();
    }

/* -----------------------------DTOS------------------------------------- */

    private VehiculoModel convertirAModelo(VehiculoRequestDto dto) {
        VehiculoModel vehiculo = new VehiculoModel();
        vehiculo.setTipoVehiculo(dto.getTipoVehiculo());
        vehiculo.setTipoServicio(dto.getTipoServicio());
        vehiculo.setTipoCombustible(dto.getTipoCombustible());
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setCapacidadPasajeros(dto.getCapacidadPasajeros());
        vehiculo.setColorHexadecimal(dto.getColorHexadecimal());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setLinea(dto.getLinea());

        /* Solo si tiene documentos entramos a recorrer el arreglo */
        if (dto.getDocumentos() != null) {
            vehiculo.setDocumentos(dto.getDocumentos().stream()
                    .map(this::convertirDocumentoAModelo)
                    .collect(Collectors.toList()));
        }

        return vehiculo;
    }

    private VehiculoDocumentoModel convertirDocumentoAModelo(VehiculoDocumentoRequestDto dto) {

        DocumentoModel documento = new DocumentoModel();
        documento.setId(dto.getDocumentoId());

        VehiculoDocumentoModel vehiculoDocumento = new VehiculoDocumentoModel();
        vehiculoDocumento.setDocumento(documento);
        vehiculoDocumento.setFechaExpedicion(dto.getFechaExpedicion());
        vehiculoDocumento.setFechaVencimiento(dto.getFechaVencimiento());
        return vehiculoDocumento;
    }

    private VehiculoResponseDto convertirAResponse(VehiculoModel modelo) {

        VehiculoResponseDto dto = new VehiculoResponseDto();
        dto.setId(modelo.getId());
        dto.setTipoVehiculo(modelo.getTipoVehiculo());
        dto.setTipoServicio(modelo.getTipoServicio());
        dto.setTipoCombustible(modelo.getTipoCombustible());
        dto.setPlaca(modelo.getPlaca());
        dto.setCapacidadPasajeros(modelo.getCapacidadPasajeros());
        dto.setColorHexadecimal(modelo.getColorHexadecimal());
        dto.setModelo(modelo.getModelo());
        dto.setMarca(modelo.getMarca());
        dto.setLinea(modelo.getLinea());
        dto.setDocumentos(modelo.getDocumentos().stream()
                .map(this::convertirDocumentoAResponse)
                .collect(Collectors.toList()));
        return dto;
    }

    private VehiculoDocumentoResponseDto convertirDocumentoAResponse(VehiculoDocumentoModel modelo) {
        
        VehiculoDocumentoResponseDto dto = new VehiculoDocumentoResponseDto();
        dto.setId(modelo.getId());
        dto.setDocumentoId(modelo.getDocumento().getId());
        dto.setFechaExpedicion(modelo.getFechaExpedicion());
        dto.setFechaVencimiento(modelo.getFechaVencimiento());
        dto.setEstadoDocumento(modelo.getEstadoDocumento());
        return dto;
    }
}