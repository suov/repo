package com.example.tutoria1.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.tutoria1.Dto.Trayecto.Request.TrayectoRequestDto;
import com.example.tutoria1.Dto.Trayecto.Response.TrayectoResponseDto;
import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Model.TrayectoModel;
import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Model.VehiculoModel;
import com.example.tutoria1.Model.VehiculoPersonaModel;
import com.example.tutoria1.Service.interfaces.TrayectoService;
import com.example.tutoria1.repository.PersonaRepository;
import com.example.tutoria1.repository.TrayectoRepository;
import com.example.tutoria1.repository.VehiculoRepository;

@Service
@Transactional
public class TrayectoServiceImpl implements TrayectoService {

    private final TrayectoRepository trayectoRepository;
    private final PersonaRepository personaRepository;
    private final VehiculoRepository vehiculoRepository;

    public TrayectoServiceImpl(
            TrayectoRepository trayectoRepository,
            PersonaRepository personaRepository,
            VehiculoRepository vehiculoRepository
    ) {
        this.trayectoRepository = trayectoRepository;
        this.personaRepository = personaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public TrayectoResponseDto crearTrayecto(
            TrayectoRequestDto request,
            String loginUsuario
    ) {

        validarRequest(request);

        PersonaModel conductor = personaRepository
                .findById(request.getConductorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "ERROR: Conductor no encontrado"
                ));

        VehiculoModel vehiculo = vehiculoRepository
                .findById(request.getVehiculoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "ERROR: Vehículo no encontrado"
                ));

        validarQueSeaConductor(conductor);
        validarRelacionConductorVehiculo(conductor, vehiculo);
        validarDocumentosVehiculo(vehiculo);
        validarParadaRuta(request);

        TrayectoModel trayecto = new TrayectoModel();

        trayecto.setConductor(conductor);
        trayecto.setVehiculo(vehiculo);
        trayecto.setCodigoRuta(request.getCodigoRuta().trim().toUpperCase());
        trayecto.setUbicacion(request.getUbicacion().trim());
        trayecto.setOrdenParada(request.getOrdenParada());
        trayecto.setLatitud(request.getLatitud());
        trayecto.setLongitud(request.getLongitud());
        trayecto.setLoginUsuario(loginUsuario);

        TrayectoModel trayectoGuardado = trayectoRepository.save(trayecto);

        return convertirAResponse(trayectoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrayectoResponseDto> consultarRutaPorCodigo(
            String codigoRuta
    ) {

        if (codigoRuta == null || codigoRuta.isBlank()) {
            throw new IllegalArgumentException(
                    "ERROR: El código de ruta es obligatorio"
            );
        }

        List<TrayectoModel> trayectos = trayectoRepository
                .findByCodigoRutaOrderByOrdenParadaAsc(
                        codigoRuta.trim().toUpperCase()
                );

        if (trayectos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "ERROR: No se encontraron paradas para la ruta indicada"
            );
        }

        return trayectos.stream()
                .map(this::convertirAResponse)
                .toList();
    }

    /* -------------------------------COMPLEMENTARIOS------------------------------- */

    private void validarRequest(TrayectoRequestDto request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "ERROR: La información del trayecto es obligatoria"
            );
        }

        if (request.getConductorId() == null) {
            throw new IllegalArgumentException(
                    "ERROR: El conductor es obligatorio"
            );
        }

        if (request.getVehiculoId() == null) {
            throw new IllegalArgumentException(
                    "ERROR: El vehículo es obligatorio"
            );
        }

        if (request.getCodigoRuta() == null
                || request.getCodigoRuta().isBlank()) {
            throw new IllegalArgumentException(
                    "ERROR: El código de ruta es obligatorio"
            );
        }

        if (request.getUbicacion() == null
                || request.getUbicacion().isBlank()) {
            throw new IllegalArgumentException(
                    "ERROR: La ubicación de la parada es obligatoria"
            );
        }

        if (request.getOrdenParada() == null
                || request.getOrdenParada() < 0) {
            throw new IllegalArgumentException(
                    "ERROR: El orden de parada debe ser cero o mayor"
            );
        }
    }

    private void validarQueSeaConductor(PersonaModel persona) {

        if (persona.getTipoPersona() == null
                || !"C".equals(persona.getTipoPersona().name())) {
            throw new IllegalArgumentException(
                    "La persona seleccionada no es un conductor"
            );
        }
    }

    private void validarRelacionConductorVehiculo(
            PersonaModel persona,
            VehiculoModel vehiculo
    ) {

        List<VehiculoPersonaModel> conductores = vehiculo.getConductores();

        if (conductores == null || conductores.isEmpty()) {
            throw new IllegalArgumentException(
                    "ERROR: El vehiculo asociado no contiene conductores."
            );
        }

        for (VehiculoPersonaModel conductor : conductores) {
            if (conductor.getId().equals(persona.getId())) {
                if (conductor.getEstadoConductor() != EstadoConductor.PO) {
                    throw new IllegalArgumentException(
                            "ERROR: La relación del Vehiculo: "
                                    + vehiculo.getPlaca() + ", con el conductor: "
                                    + persona.getNombre() + ", no esta disponible para operar."
                    );
                }
            }
        }
    }

    private void validarDocumentosVehiculo(
            VehiculoModel vehiculo
    ) {

        if (vehiculo == null || vehiculo.getDocumentos() == null
                || vehiculo.getDocumentos().isEmpty()) {

            throw new IllegalArgumentException(
                    "ERROR: El vehículo asociado no tiene documentos registrados."
            );
        }

        for (VehiculoDocumentoModel documento : vehiculo.getDocumentos()) {
            if (documento == null
                    || documento.getEstadoDocumento() != VehiculoDocumentoStatus.HABILITADO) {
                throw new IllegalArgumentException(
                        "ERROR: El vehiculo: " + vehiculo.getPlaca()
                        + ", no puede operar debido a un documento no habilitado."
                );
            }
        }
    }

    private void validarParadaRuta(
            TrayectoRequestDto request
    ) {

        String codigoRuta = request.getCodigoRuta().trim().toUpperCase();

        List<TrayectoModel> paradasExistentes = trayectoRepository
                .findByCodigoRutaOrderByOrdenParadaAsc(codigoRuta);

        if (paradasExistentes.size() > 7) {
            throw new IllegalArgumentException(
                    "ERROR: Una ruta permite máximo 7 paradas: inicio, fin y 5 intermedias"
            );
        }

        boolean ordenRepetido = paradasExistentes.stream()
                .anyMatch(parada ->
                        parada.getOrdenParada()
                                .equals(request.getOrdenParada())
                );

        if (ordenRepetido) {
            throw new IllegalArgumentException(
                    "ERROR: Ya existe una parada con ese orden para esta ruta"
            );
        }
    }

    private TrayectoResponseDto convertirAResponse(
            TrayectoModel modelo
    ) {

        TrayectoResponseDto dto = new TrayectoResponseDto();

        dto.setId(modelo.getId());
        dto.setConductorId(modelo.getConductor().getId());
        dto.setNombreConductor(
                modelo.getConductor().getNombre()
                        + " "
                        + modelo.getConductor().getApellido()
        );
        dto.setVehiculoId(modelo.getVehiculo().getId());
        dto.setPlacaVehiculo(modelo.getVehiculo().getPlaca());
        dto.setCodigoRuta(modelo.getCodigoRuta());
        dto.setUbicacion(modelo.getUbicacion());
        dto.setOrdenParada(modelo.getOrdenParada());
        dto.setLatitud(modelo.getLatitud());
        dto.setLongitud(modelo.getLongitud());

        return dto;
    }

}