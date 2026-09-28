package com.example.tutoria1.Controller;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import com.example.tutoria1.Dto.VehiculoDocumento.Request.VehiculoDocumentoRequestDto;
import com.example.tutoria1.Dto.Exception.ApiResponseDTO;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @PostMapping
    public ResponseEntity<Object> crearVehiculo(
            @RequestBody VehiculoRequestDto vehiculoRequest
    ) {

        VehiculoModel vehiculoCreado = vehiculoService.crearVehiculo(
                convertirAModelo(vehiculoRequest)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponseDTO<>(
                                "Vehículo creado correctamente",
                                convertirAResponse(vehiculoCreado)
                        )
                );
    }

    @GetMapping
    public List<VehiculoResponseDto> listarVehiculos() {

        return vehiculoService.listarVehiculos()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<Object> buscarVehiculoPorPlaca(
            @PathVariable String placa
    ) {

        VehiculoModel vehiculo = vehiculoService
                .consultarVehiculoPorPlaca(placa)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vehículo no encontrado"
                ));

        return ResponseEntity.ok(convertirAResponse(vehiculo));
    }

    @GetMapping("/tipo/{tipoVehiculo}")
    public ResponseEntity<Object> buscarVehiculosPorTipoVehiculo(
            @PathVariable String tipoVehiculo
    ) {

        List<VehiculoModel> vehiculos = vehiculoService
                .consultarVehiculosPorTipoVehiculo(tipoVehiculo);

        if (vehiculos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encuentran vehículos de ese tipo"
            );
        }

        return ResponseEntity.ok(
                vehiculos.stream()
                        .map(this::convertirAResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/tipoDocumento/{codigoDocumento}")
    public ResponseEntity<Object> buscarVehiculosPorTipoDocumento(
            @PathVariable String codigoDocumento
    ) {

        List<VehiculoModel> vehiculos = vehiculoService
                .consultarVehiculosPorTipoDocumento(codigoDocumento);

        if (vehiculos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encuentran vehículos con ese tipo de documento asociado"
            );
        }

        return ResponseEntity.ok(
                vehiculos.stream()
                        .map(this::convertirAResponse)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/{vehiculoId}/documentos")
    public ResponseEntity<Object> agregarDocumentos(
            @PathVariable Long vehiculoId,
            @RequestBody List<VehiculoDocumentoRequestDto> documentosRequest
    ) {

        if (documentosRequest == null || documentosRequest.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes enviar al menos un documento"
            );
        }

        List<VehiculoDocumentoModel> documentos = vehiculoService.agregarDocumentos(
                vehiculoId,
                documentosRequest
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponseDTO<>(
                                "Documentos asociados al vehículo correctamente",
                                documentos.stream()
                                        .map(this::convertirDocumentoAResponse)
                                        .collect(Collectors.toList())
                        )
                );
    }

    @GetMapping("/estadoDocumento/{estadoDocumento}")
    public ResponseEntity<Object> buscarVehiculosPorEstadoDocumento(
            @PathVariable String estadoDocumento
    ) {

        List<VehiculoModel> vehiculos = vehiculoService
                .consultarVehiculosPorEstadoDocumento(estadoDocumento);

        if (vehiculos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encuentran vehículos con documentos en ese estado"
            );
        }

        return ResponseEntity.ok(
                vehiculos.stream()
                        .map(this::convertirAResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public VehiculoResponseDto obtenerVehiculoPorId(
            @PathVariable Long id
    ) {

        return convertirAResponse(
                vehiculoService.obtenerVehiculoPorId(id)
        );
    }

    @PutMapping("/{id}")
    public VehiculoResponseDto actualizarVehiculo(
            @PathVariable Long id,
            @RequestBody VehiculoRequestDto vehiculoRequest
    ) {

        return convertirAResponse(
                vehiculoService.actualizarVehiculo(
                        id,
                        convertirAModelo(vehiculoRequest)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarVehiculo(
            @PathVariable Long id
    ) {

        VehiculoModel vehiculoEliminado =
                vehiculoService.obtenerVehiculoPorId(id);

        vehiculoService.eliminarVehiculo(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ApiResponseDTO<>(
                                "Vehículo eliminado correctamente",
                                convertirAResponse(vehiculoEliminado)
                        )
                );
    }

    /* TUTORÍA 2 */
    @GetMapping("/por-vencer")
    public ResponseEntity<Object> buscarVehiculosPorVencer(
            @RequestParam(defaultValue = "30") Integer dias
    ) {

        List<VehiculoModel> vehiculos = vehiculoService.listarVehiculos().stream()
                .filter(vehiculo -> vehiculo.getDocumentos() != null && !vehiculo.getDocumentos().isEmpty())
                .filter(vehiculo -> vehiculo.getDocumentos().stream().anyMatch(documento ->
                        documento.getFechaVencimiento() != null &&
                                documento.getFechaVencimiento().isBefore(java.time.LocalDate.now().plusDays(dias))))
                .toList();

        if (vehiculos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encuentran vehículos con documentos por vencer");
        }

        return ResponseEntity.ok(vehiculos.stream().map(this::convertirAResponse).collect(Collectors.toList()));
    }

    @GetMapping("/vencidos")
    public ResponseEntity<Object> buscarVehiculosVencidos() {

        List<VehiculoModel> vehiculos = vehiculoService.consultarVehiculosPorEstadoDocumento("VENCIDO");

        if (vehiculos.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encuentran vehículos con documentos vencidos"
                );
        }

        return ResponseEntity.ok(vehiculos.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList()));
    }

    /* MAPPERS */
    private VehiculoModel convertirAModelo(
            VehiculoRequestDto dto
    ) {

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

        if (dto.getDocumentos() != null) {
            vehiculo.setDocumentos(
                    dto.getDocumentos()
                            .stream()
                            .map(this::convertirDocumentoAModelo)
                            .collect(Collectors.toList())
            );
        }

        return vehiculo;
    }

    private VehiculoDocumentoModel convertirDocumentoAModelo(
            VehiculoDocumentoRequestDto dto
    ) {

        DocumentoModel documento = new DocumentoModel();
        documento.setId(dto.getDocumentoId());

        VehiculoDocumentoModel vehiculoDocumento =
                new VehiculoDocumentoModel();

        vehiculoDocumento.setDocumento(documento);
        vehiculoDocumento.setNombreArchivo(dto.getNombreArchivo());
        vehiculoDocumento.setArchivoBase64(dto.getArchivoBase64());
        vehiculoDocumento.setFechaExpedicion(dto.getFechaExpedicion());
        vehiculoDocumento.setFechaVencimiento(dto.getFechaVencimiento());

        String base64Pdf = dto.getArchivoBase64();
        if ((base64Pdf == null || base64Pdf.isBlank()) && dto.getArchivoPdfBase64() != null) {
            base64Pdf = dto.getArchivoPdfBase64();
        }

        if (base64Pdf != null && !base64Pdf.isBlank()) {
            String limpio = base64Pdf
                    .replace("data:application/pdf;base64,", "")
                    .replace("data:application/octet-stream;base64,", "")
                    .trim();

            try {
                byte[] archivoPdf = Base64.getDecoder()
                        .decode(limpio);

                vehiculoDocumento.setArchivoPdf(archivoPdf);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        "El archivo PDF no tiene un formato Base64 válido"
                );
            }
        }

        return vehiculoDocumento;
    }

    private VehiculoResponseDto convertirAResponse(
            VehiculoModel modelo
    ) {

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

        dto.setDocumentos(
                modelo.getDocumentos() == null ? List.of() : modelo.getDocumentos()
                        .stream()
                        .map(this::convertirDocumentoAResponse)
                        .collect(Collectors.toList())
        );

        dto.setConductores(
                modelo.getConductores() == null ? List.of() : modelo.getConductores()
                        .stream()
                        .map(asociacion -> new com.example.tutoria1.Dto.VehiculoPersona.Response.VehiculoPersonaResponseDto(
                                asociacion.getId(),
                                asociacion.getVehiculo().getId(),
                                asociacion.getVehiculo().getPlaca(),
                                asociacion.getPersona().getId(),
                                asociacion.getPersona().getNombre() + " " + asociacion.getPersona().getApellido(),
                                asociacion.getFechaAsociacion(),
                                asociacion.getEstadoConductor()))
                        .collect(Collectors.toList())
        );

        return dto;
    }

    private VehiculoDocumentoResponseDto convertirDocumentoAResponse(
            VehiculoDocumentoModel modelo
    ) {

        VehiculoDocumentoResponseDto dto =
                new VehiculoDocumentoResponseDto();

        dto.setId(modelo.getId());
        dto.setDocumentoId(modelo.getDocumento().getId());
        dto.setNombreArchivo(modelo.getNombreArchivo());
        dto.setFechaExpedicion(modelo.getFechaExpedicion());
        dto.setFechaVencimiento(modelo.getFechaVencimiento());
        dto.setEstadoDocumento(modelo.getEstadoDocumento());
        dto.setArchivoBase64(modelo.getArchivoBase64());

        if (modelo.getArchivoPdf() != null) {
            String archivoPdfBase64 = Base64.getEncoder()
                    .encodeToString(modelo.getArchivoPdf());

            dto.setArchivoPdfBase64(archivoPdfBase64);
        } else if (modelo.getArchivoBase64() != null && !modelo.getArchivoBase64().isBlank()) {
            dto.setArchivoPdfBase64(modelo.getArchivoBase64());
        }

        return dto;
    }
}