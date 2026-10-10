package com.example.tutoria1.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.tutoria1.Enums.Persona.TipoPersona;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Model.UsuarioModel;
import com.example.tutoria1.Model.UsuarioPKModel;
import com.example.tutoria1.Service.interfaces.IPersonaService;
import com.example.tutoria1.repository.PersonaRepository;
import com.example.tutoria1.repository.UsuarioRepository;

@Service
public class PersonaService implements IPersonaService {

    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;

    public PersonaService(
        PersonaRepository personaRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.personaRepository = personaRepository;
        this.usuarioRepository = usuarioRepository;
    };

    /* Post  - OK */
    @Override
    public PersonaModel crearPersona(PersonaModel persona) {

        validarLicenciaConduccion(persona);

        if(persona.getNombre() == null
            || persona.getApellido() == null
            || persona.getDocumento() == null
            || persona.getTipoPersona() == null
            || persona.getTipoDocumento() == null
        ) {
            throw new IllegalArgumentException("No hay suficiente información para crear la persona");
        }

        PersonaModel personaCreada = personaRepository.save(persona);

        if(personaCreada.getTipoPersona() == TipoPersona.A) {

            /* Creamos el usuario asociado */
            UsuarioModel usuario = new UsuarioModel();
            /* Login */
            usuario.setUsuarioPK(new UsuarioPKModel(generarLogin(personaCreada), personaCreada.getId()));
            /* idPersona */
            usuario.setPersona(personaCreada);
            /* Password */
            usuario.setPassword(personaCreada.getDocumento());
            /* Apikey */
            usuario.setApikey(UUID.randomUUID().toString());

            usuarioRepository.save(usuario);
        }

        return personaCreada;
    };

    /* Get - OK */
    @Override
    public List<PersonaModel> listarPersonas() {
        
        return personaRepository.findAll();
    };

    /* Put - OK */
    @Override
    public PersonaModel actualizarPersona(PersonaModel persona, Long id) {

        if (persona == null) {
            throw new IllegalArgumentException("La información de la persona es obligatoria");
        }

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Ingrese un Id valido para realizar la busqueda");
        }

        if (!personaRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario con ese ID no fue encontrado");
        }

        return personaRepository.findById(id)
                .map(personaActualizado -> {
                    if (persona.getTipoPersona() == TipoPersona.C) {
                        if (persona.getLicenciaConduccion() != null) {
                            personaActualizado.setLicenciaConduccion(
                                    persona.getLicenciaConduccion()
                            );
                        }
                        if (persona.getFechaVigenciaLicencia() != null) {
                            personaActualizado.setFechaVigenciaLicencia(
                                    persona.getFechaVigenciaLicencia()
                            );
                        }
                    } else {
                        validarLicenciaConduccion(persona);
                        personaActualizado.setLicenciaConduccion(null);
                        personaActualizado.setFechaVigenciaLicencia(null);
                    }

                    personaActualizado.setNombre(persona.getNombre());
                    personaActualizado.setApellido(persona.getApellido());
                    personaActualizado.setEmail(persona.getEmail());
                    personaActualizado.setTipoDocumento(persona.getTipoDocumento());
                    personaActualizado.setTipoPersona(persona.getTipoPersona());
                    validarLicenciaConduccion(personaActualizado);
                    return personaRepository.save(personaActualizado);
                }).orElseThrow(
                        () -> new RuntimeException("Usuario No encontrado con ID: " + id));
    }

    private void validarLicenciaConduccion(PersonaModel persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona es obligatoria");
        }

        if (persona.getTipoPersona() == TipoPersona.C) {
            if (persona.getLicenciaConduccion() == null
                    || persona.getLicenciaConduccion().length == 0) {
                throw new IllegalArgumentException(
                        "La licencia de conducción en Base64 es obligatoria para conductores"
                );
            }
            if (persona.getFechaVigenciaLicencia() == null) {
                throw new IllegalArgumentException(
                        "La fecha de vigencia de la licencia es obligatoria para conductores"
                );
            }
        } else if (persona.getLicenciaConduccion() != null
                || persona.getFechaVigenciaLicencia() != null) {
            throw new IllegalArgumentException(
                    "La licencia y su fecha de vigencia solo aplican a personas tipo conductor"
            );
        }
    }

    /* Complemento */
    private String generarLogin(PersonaModel persona) {
        if (persona == null) {
            throw new IllegalStateException("La persona es obligatoria para crear el usuario.");
        }

        String nombre = persona.getNombre();
        String apellido = persona.getApellido();
        String documento = persona.getDocumento();

        if (nombre == null 
            || nombre.isBlank() 
            || apellido == null 
            || apellido.isBlank() 
            || documento == null 
            || documento.isBlank()) {
            throw new IllegalStateException("Falta información de la persona para generar el login.");
        }

        /* Nemotecnia */
        String primeraLetraNombre = nombre.substring(0, 1).toUpperCase();
        String primeraLetraApellido = apellido.substring(0, 1).toUpperCase();

        return primeraLetraNombre + primeraLetraApellido + documento.trim();
    }
}
