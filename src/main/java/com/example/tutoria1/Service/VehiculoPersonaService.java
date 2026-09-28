package com.example.tutoria1.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tutoria1.Dto.VehiculoPersona.Request.VehiculoPersonaRequestDto;
import com.example.tutoria1.Dto.VehiculoPersona.Response.VehiculoPersonaResponseDto;
import com.example.tutoria1.Enums.Persona.TipoPersona;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Model.VehiculoModel;
import com.example.tutoria1.Model.VehiculoPersonaModel;
import com.example.tutoria1.Service.interfaces.IVehiculoPersonaService;
import com.example.tutoria1.repository.PersonaRepository;
import com.example.tutoria1.repository.VehiculoPersonaRepository;
import com.example.tutoria1.repository.VehiculoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculoPersonaService implements IVehiculoPersonaService {

    private final VehiculoPersonaRepository vehiculoPersonaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PersonaRepository personaRepository;

    @Override
    public VehiculoPersonaResponseDto crearAsociacion(
            VehiculoPersonaRequestDto requestDto
    ) {
        VehiculoModel vehiculo = vehiculoRepository
                .findById(requestDto.getIdVehiculo())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un vehículo con el ID indicado"
                ));

        PersonaModel persona = personaRepository
                .findById(requestDto.getIdPersona())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una persona con el ID indicado"
                ));

        if (persona.getTipoPersona() != TipoPersona.C) {
            throw new IllegalArgumentException(
                    "La persona debe tener tipo C (Conductor)"
            );
        }

        if (vehiculoPersonaRepository.existsByVehiculoIdAndPersonaId(
                vehiculo.getId(),
                persona.getId()
        )) {
            throw new IllegalArgumentException(
                    "Esta persona ya está asociada al vehículo"
            );
        }

        if (vehiculoPersonaRepository.countByVehiculoId(
                vehiculo.getId()
        ) >= 5) {
            throw new IllegalStateException(
                    "Un vehículo no puede tener más de 5 conductores"
            );
        }

        VehiculoPersonaModel asociacion = new VehiculoPersonaModel();

        asociacion.setVehiculo(vehiculo);
        asociacion.setPersona(persona);
        asociacion.setFechaAsociacion(requestDto.getFechaAsociacion());
        asociacion.setEstadoConductor(requestDto.getEstadoConductor());

        VehiculoPersonaModel asociacionGuardada =
                vehiculoPersonaRepository.save(asociacion);

        return convertirAResponse(asociacionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoPersonaResponseDto> listarTodos() {
        return vehiculoPersonaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoPersonaResponseDto> listarPorVehiculo(
            Long idVehiculo
    ) {
        if (!vehiculoRepository.existsById(idVehiculo)) {
            throw new IllegalArgumentException(
                    "No existe un vehículo con el ID indicado"
            );
        }

        return vehiculoPersonaRepository.findByVehiculoId(idVehiculo)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Override
    public VehiculoPersonaResponseDto actualizarEstado(
            Long idAsociacion,
            EstadoConductor estadoConductor
    ) {
        
        VehiculoPersonaModel asociacion = vehiculoPersonaRepository
                .findById(idAsociacion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la asociación indicada"
                ));

        asociacion.setEstadoConductor(estadoConductor);

        VehiculoPersonaModel asociacionActualizada =
                vehiculoPersonaRepository.save(asociacion);

        return convertirAResponse(asociacionActualizada);
    }

    @Override
    public void eliminarAsociacion(Long idAsociacion) {
        VehiculoPersonaModel asociacion = vehiculoPersonaRepository
                .findById(idAsociacion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la asociación indicada"
                ));

        long cantidadConductores = vehiculoPersonaRepository
                .countByVehiculoId(asociacion.getVehiculo().getId());

        if (cantidadConductores <= 1) {
            throw new IllegalStateException(
                    "No se puede eliminar el último conductor del vehículo"
            );
        }

        vehiculoPersonaRepository.delete(asociacion);
    }

    private VehiculoPersonaResponseDto convertirAResponse(
            VehiculoPersonaModel asociacion
    ) {
        return new VehiculoPersonaResponseDto(
                asociacion.getId(),
                asociacion.getVehiculo().getId(),
                asociacion.getVehiculo().getPlaca(),
                asociacion.getPersona().getId(),
                asociacion.getPersona().getNombre()
                        + " "
                        + asociacion.getPersona().getApellido(),
                asociacion.getFechaAsociacion(),
                asociacion.getEstadoConductor()
        );
    }
}