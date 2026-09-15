package com.example.tutoria1.Service;

import com.example.tutoria1.Enums.Vehiculo.TipoVehiculo;
import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Model.DocumentoModel;
import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Model.VehiculoModel;
import com.example.tutoria1.Service.interfaces.VehiculoService;
import com.example.tutoria1.repository.DocumentoRepository;
import com.example.tutoria1.repository.VehiculoRepository;
import com.example.tutoria1.repository.VehiculoDocumentoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final DocumentoRepository documentoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final VehiculoDocumentoRepository vehiculoDocumentoRepository;

    public VehiculoServiceImpl(
        VehiculoRepository vehiculoRepository,
        DocumentoRepository documentoRepository,
        VehiculoDocumentoRepository vehiculoDocumentoRepository
    ) {
        this.vehiculoRepository = vehiculoRepository;
        this.documentoRepository = documentoRepository;
        this.vehiculoDocumentoRepository = vehiculoDocumentoRepository;
    }

    @Override
    public VehiculoModel crearVehiculo(VehiculoModel vehiculo) {

        /* Validaciones ----------------------------------------------------- */
        if (vehiculo.getPlaca() == null || vehiculo.getPlaca().isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria");
        }

        if (vehiculo.getDocumentos() == null || vehiculo.getDocumentos().isEmpty()) {
            throw new IllegalArgumentException("No es posible crear un vehiculo sin documentos");
        }

        vehiculo.setPlaca(vehiculo.getPlaca().trim().toUpperCase());
        validarFormatoPlaca(vehiculo);

        if (vehiculoRepository.existsByPlaca(vehiculo.getPlaca())) {
            throw new IllegalArgumentException("La placa ya está registrada");
        }

        /* Creación de relación entre vehiculo y documentos ---------------------------*/
        for (VehiculoDocumentoModel vehiculoDocumento : vehiculo.getDocumentos()){
            if (vehiculoDocumento.getDocumento() == null || vehiculoDocumento.getDocumento().getId() == null){
                throw new IllegalArgumentException("Cada documento debe tener un id");
            }
    
            DocumentoModel documento = documentoRepository
                .findById(vehiculoDocumento.getDocumento().getId())
                .orElseThrow(() -> new IllegalArgumentException("El documento no existe"));
            
            vehiculoDocumento.setVehiculo(vehiculo);
            vehiculoDocumento.setDocumento(documento);
            vehiculoDocumento.setEstadoDocumento(VehiculoDocumentoStatus.EN_VERIFICACION);
        }

        return vehiculoRepository.save(vehiculo);
    }

    private void validarFormatoPlaca(VehiculoModel vehiculo) {

        String placa = vehiculo.getPlaca();

        boolean placaAutomovil = placa.matches("^[A-Z]{3}[0-9]{3}$");
        boolean placaMotocicleta = placa.matches("^[A-Z]{3}[0-9]{2}[A-Z]$");

        if (vehiculo.getTipoVehiculo() == TipoVehiculo.AUTOMOVIL && !placaAutomovil) {
            throw new IllegalArgumentException(
                    "La placa de automóvil debe tener formato ABC123");
        }

        if (vehiculo.getTipoVehiculo() == TipoVehiculo.MOTOCICLETA && !placaMotocicleta) {
            throw new IllegalArgumentException(
                    "La placa de motocicleta debe tener formato ABC12D");
        }
    }

    @Override
    public List<VehiculoModel> listarVehiculos() {
        return vehiculoRepository.findAll();
    }

    @Override
    public VehiculoModel obtenerVehiculoPorId(Long id) {

        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vehículo no encontrado"));
    }

    @Override
    public VehiculoModel actualizarVehiculo(Long id, VehiculoModel vehiculo) {

        VehiculoModel vehiculoExistente = obtenerVehiculoPorId(id);

        if (vehiculo.getPlaca() == null || vehiculo.getPlaca().isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria");
        }

        vehiculo.setPlaca(vehiculo.getPlaca().trim().toUpperCase());

        validarFormatoPlaca(vehiculo);

        boolean cambioPlaca = !vehiculoExistente.getPlaca()
                .equals(vehiculo.getPlaca());

        if (cambioPlaca && vehiculoRepository.existsByPlaca(vehiculo.getPlaca())) {
            throw new IllegalArgumentException("La placa ya está registrada");
        }

        vehiculoExistente.setTipoVehiculo(vehiculo.getTipoVehiculo());
        vehiculoExistente.setTipoServicio(vehiculo.getTipoServicio());
        vehiculoExistente.setTipoCombustible(vehiculo.getTipoCombustible());
        vehiculoExistente.setPlaca(vehiculo.getPlaca());
        vehiculoExistente.setCapacidadPasajeros(vehiculo.getCapacidadPasajeros());
        vehiculoExistente.setColorHexadecimal(vehiculo.getColorHexadecimal());
        vehiculoExistente.setModelo(vehiculo.getModelo());
        vehiculoExistente.setMarca(vehiculo.getMarca());
        vehiculoExistente.setLinea(vehiculo.getLinea());

        return vehiculoRepository.save(vehiculoExistente);
    }

    @Override
    public void eliminarVehiculo(Long id) {

        VehiculoModel vehiculo = obtenerVehiculoPorId(id);
        vehiculoRepository.delete(vehiculo);
    }

    /* Consultas tipo filtro ------------------------------------- */
    @Override
    public Optional<VehiculoModel> consultarVehiculoPorPlaca(String placa) {

        if (placa == null) {
            return Optional.empty();
        }
        return vehiculoRepository.findByPlaca(placa.trim().toUpperCase());
    }

    @Override
    public List<VehiculoModel> consultarVehiculosPorTipoVehiculo(String tipoVehiculo) {

        try {
            TipoVehiculo tipo = TipoVehiculo.valueOf(tipoVehiculo.trim().toUpperCase());
            return vehiculoRepository.findByTipoVehiculo(tipo);
        } catch (IllegalArgumentException exception) {
            return List.of();
        }
    }

    @Override
    public List<VehiculoModel> consultarVehiculosPorTipoDocumento(String codigoDocumento) {
        if (codigoDocumento == null || codigoDocumento.isBlank()) {
            return List.of();
        }
        return vehiculoRepository.findDistinctByDocumentosDocumentoCodigoDocumentoParametrizado(
                codigoDocumento.trim());
    }

    @Override
    public VehiculoDocumentoModel agregarDocumento(
            Long vehiculoId,
            VehiculoDocumentoModel vehiculoDocumento) {

        VehiculoModel vehiculo = obtenerVehiculoPorId(vehiculoId);
        if (vehiculoDocumento == null
                || vehiculoDocumento.getDocumento() == null
                || vehiculoDocumento.getDocumento().getId() == null) {
            throw new IllegalArgumentException("El documento asociado es obligatorio");
        }

        DocumentoModel documento = documentoRepository.findById(
                vehiculoDocumento.getDocumento().getId())
                .orElseThrow(() -> new IllegalArgumentException("El documento no existe"));

        if (vehiculoDocumento.getFechaExpedicion() == null
            || vehiculoDocumento.getFechaVencimiento() == null
            || vehiculoDocumento.getFechaVencimiento()
                .isBefore(vehiculoDocumento.getFechaExpedicion())) {
            throw new IllegalArgumentException("Las fechas del documento son obligatorias");
        }

        vehiculoDocumento.setVehiculo(vehiculo);
        vehiculoDocumento.setDocumento(documento);
        vehiculoDocumento.setEstadoDocumento(VehiculoDocumentoStatus.EN_VERIFICACION);
        return vehiculoDocumentoRepository.save(vehiculoDocumento);
    }

}
