package com.example.tutoria1.Service;

import com.example.tutoria1.Enums.Vehiculo.TipoVehiculo;
import com.example.tutoria1.Model.VehiculoModel;
import com.example.tutoria1.Service.interfaces.VehiculoService;
import com.example.tutoria1.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public VehiculoModel crearVehiculo(VehiculoModel vehiculo) {
        if (vehiculo.getPlaca() == null || vehiculo.getPlaca().isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria");
        }

        vehiculo.setPlaca(vehiculo.getPlaca().trim().toUpperCase());

        validarFormatoPlaca(vehiculo);

        if (vehiculoRepository.existsByPlaca(vehiculo.getPlaca())) {
            throw new IllegalArgumentException("La placa ya está registrada");
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

    @Override
    public Optional<VehiculoModel> consultarVehiculoPorPlaca(String placa) {
        return vehiculoRepository.findByPlaca(placa);
    }

    @Override
    public Optional<VehiculoModel> consultarVehiculoPorTipVehiculo(String tipoVehiculo) {
        return vehiculoRepository.findByTipoVehiculo(tipoVehiculo);
    }

}
