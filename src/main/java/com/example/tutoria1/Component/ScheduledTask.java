package com.example.tutoria1.Component;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.tutoria1.Enums.Persona.TipoPersona;
import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Model.VehiculoPersonaModel;
import com.example.tutoria1.repository.PersonaRepository;
import com.example.tutoria1.repository.VehiculoDocumentoRepository;
import com.example.tutoria1.repository.VehiculoPersonaRepository;

@Component 
public class ScheduledTask {
    
    // ========= INYECCIÓN DE DEPENDENCIAS ==========
 	@Qualifier ("IPersonaRepository")
    private final PersonaRepository personaRepository;

    @Qualifier ("IVehiculoPersonaRepository")
    private final VehiculoPersonaRepository vehiculoPersonaRepository;

    @Qualifier ("IVehiculoDocumentoRepository")
    private final VehiculoDocumentoRepository vehiculoDocumentoRepository;

    ScheduledTask(
        PersonaRepository personaRepository,
        VehiculoPersonaRepository vehiculoPersonaRepository,
        VehiculoDocumentoRepository vehiculoDocumentoRepository
    ) {
        this.personaRepository = personaRepository;
        this.vehiculoPersonaRepository = vehiculoPersonaRepository;
        this.vehiculoDocumentoRepository = vehiculoDocumentoRepository;
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
        
    }
}
