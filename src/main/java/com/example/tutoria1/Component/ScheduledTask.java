package com.example.tutoria1.Component;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.tutoria1.APIs.GoogleMaps.Geocoder;
import com.example.tutoria1.Enums.Persona.TipoPersona;
import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Model.TrayectoModel;
import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Model.VehiculoPersonaModel;
import com.example.tutoria1.repository.PersonaRepository;
import com.example.tutoria1.repository.TrayectoRepository;
import com.example.tutoria1.repository.VehiculoDocumentoRepository;
import com.example.tutoria1.repository.VehiculoPersonaRepository;

@Component 
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    private final Geocoder geocoder;
    
    // ========= INYECCIÓN DE DEPENDENCIAS ==========
 	@Qualifier ("IPersonaRepository")
    private final PersonaRepository personaRepository;

    @Qualifier ("IVehiculoPersonaRepository")
    private final VehiculoPersonaRepository vehiculoPersonaRepository;

    @Qualifier ("IVehiculoDocumentoRepository")
    private final VehiculoDocumentoRepository vehiculoDocumentoRepository;

    @Qualifier ("ITrayectoRepository")
    private final TrayectoRepository trayectoRepository;

    ScheduledTask(
        PersonaRepository personaRepository,
        VehiculoPersonaRepository vehiculoPersonaRepository,
        VehiculoDocumentoRepository vehiculoDocumentoRepository,
        TrayectoRepository trayectoRepository,
        Geocoder geocoder
    ) {
        this.personaRepository = personaRepository;
        this.vehiculoPersonaRepository = vehiculoPersonaRepository;
        this.vehiculoDocumentoRepository = vehiculoDocumentoRepository;
        this.trayectoRepository = trayectoRepository;
        this.geocoder = geocoder;
    }

    @Scheduled (cron = "0 */2 * * * *")
    public void scheduleTaskWithCronExpression(){

        LocalDate hoy = LocalDate.now();
        List<PersonaModel> personas = personaRepository.findAll();

        for (PersonaModel persona : personas) {
            if (persona.getTipoPersona() != TipoPersona.C
                || persona.getFechaVigenciaLicencia() == null
                || !persona.getFechaVigenciaLicencia().isBefore(hoy)){

                    continue;
            } else {
                List<VehiculoPersonaModel> vehiculosPersona = 
                    vehiculoPersonaRepository.findByPersonaId(persona.getId());

                for (VehiculoPersonaModel vehiculoPersona : vehiculosPersona){
                    vehiculoPersona.setEstadoConductor(EstadoConductor.RO);
                }

                vehiculoPersonaRepository.saveAll(vehiculosPersona);
            }
        }

        /* Tarea 2 */
        List<VehiculoDocumentoModel> vehiculoDocumentos = vehiculoDocumentoRepository.findAll();

        if (vehiculoDocumentos == null || vehiculoDocumentos.isEmpty()){
            System.out.println("ERROR: No se pudo recuperar la lista de documentos");
        } else {

            for (VehiculoDocumentoModel vehiculoDocumento : vehiculoDocumentos){

                if (vehiculoDocumento.getFechaVencimiento() == null 
                    || !vehiculoDocumento.getFechaVencimiento().isBefore(hoy)
                    || vehiculoDocumento.getEstadoDocumento() == VehiculoDocumentoStatus.VENCIDO){
    
                        continue;
                }
                
                vehiculoDocumento.setEstadoDocumento(VehiculoDocumentoStatus.VENCIDO);
                vehiculoDocumentoRepository.save(vehiculoDocumento);
            }
        }

    }

    @Scheduled (fixedDelay = 90_000)
    public void scheduleTaskWithFixedDelay(){

        List<TrayectoModel> trayectos = trayectoRepository.findAll();

        if (trayectos == null || trayectos.isEmpty()) {
            logger.info("ERROR: No se encontraron trayectos para evaluar coordenadas");
            return;
        }

        for (TrayectoModel trayecto : trayectos) {

            /* Si el trayecto evaluado tiene longitud y longitud. Pasamos al siguiente */
            if (trayecto.getLatitud() != null && trayecto.getLongitud() != null) {
                continue;
            }

            /* Si el trayecto evaluado no tiene la columna de ubicación se avisa y se salta */
            if (trayecto.getUbicacion() == null || trayecto.getUbicacion().isBlank()) {
                logger.warn(
                        "No se puede buscar coordenadas para el trayecto {}: ubicación vacía",
                        trayecto.getId()
                );
                continue;
            }

            try {
                String[] coordenadas = geocoder.getLatLng(trayecto.getUbicacion()).split(",", -1);
                if (coordenadas.length != 2) {
                    throw new IllegalStateException(
                            "Google Maps devolvió coordenadas con un formato inesperado"
                    );
                }

                BigDecimal latitud = new BigDecimal(coordenadas[0].trim());
                BigDecimal longitud = new BigDecimal(coordenadas[1].trim());

                if (trayecto.getLatitud() == null) {
                    trayecto.setLatitud(latitud);
                }
                if (trayecto.getLongitud() == null) {
                    trayecto.setLongitud(longitud);
                }

                trayectoRepository.save(trayecto);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                logger.warn(
                        "Se interrumpió la consulta de Google Maps para el trayecto {}",
                        trayecto.getId(),
                        exception
                );
                return;
            } catch (java.io.IOException | RuntimeException exception) {
                logger.error(
                        "No se pudieron obtener o guardar coordenadas para el trayecto {}",
                        trayecto.getId(),
                        exception
                );
            }
        }
    }
}
