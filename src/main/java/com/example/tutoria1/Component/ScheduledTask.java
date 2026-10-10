package com.example.tutoria1.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Model.VehiculoPersonaModel;
import com.example.tutoria1.repository.PersonaRepository;
import com.example.tutoria1.repository.VehiculoPersonaRepository;

@Component 
public class ScheduledTask {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    // ========= INYECCIÓN DE DEPENDENCIAS ==========
 	@Qualifier ("IPersonaRepository")
    private final PersonaRepository personaRepository;

    @Qualifier ("IVehiculoPersonaRepository")
    private final VehiculoPersonaRepository vehiculoPersonaRepository;

    ScheduledTask(
        PersonaRepository personaRepository,
        VehiculoPersonaRepository vehiculoPersonaRepository
    ) {
        this.personaRepository = personaRepository;
        this.vehiculoPersonaRepository = vehiculoPersonaRepository;
    }

    @Scheduled (cron = "*/120 * * * * ?")
    public void scheduleTaskWithCronExpression(){

        logger.info("Cron Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            List<PersonaModel> personas = personaRepository.findAll();
            if (personas != null) {
                if (personas.size() > 0) {
                    for (PersonaModel persona : personas) {
                        LocalDate fechaActual = LocalDate.now();
                        if (persona.getFechaVigenciaLicencia().isBefore(fechaActual)){
                            List<VehiculoPersonaModel> relacionesDelConductor = vehiculoPersonaRepository.findByPersonaId(persona.getId());
                            if (relacionesDelConductor != null) {
                                for (VehiculoPersonaModel relacion : relacionesDelConductor){
                                    relacion.setEstadoConductor(EstadoConductor.RO);
                                }
                            } else {
                                throw new IllegalArgumentException("ERROR: No se encuentran relaciones del conductor con vehículos");
                            }
                        }
                    }
                } else {
                    throw new IllegalArgumentException("ERROR: No se encuentran personas para evaluar licencias");
                }
            } else {
                throw new IllegalArgumentException("ERROR: No se encuentran personas para evaluar licencias");
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
