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
            usuario.setUsuarioPK(new UsuarioPKModel(generarLogin(persona), persona.getId()));
            /* idPersona */
            usuario.setPersona(persona);
            /* Password */
            usuario.setPassword(persona.getDocumento());
            /* Apikey */
            usuario.setApikey(UUID.randomUUID().toString());

            usuarioRepository.save(usuario);
        }

        return personaCreada;
    };

    /* Get */
    @Override
    public List<PersonaModel> listarPersonas() {
        List<PersonaModel> personas = personaRepository.findAll();
        return personas;
    };

    /* Put */
    @Override
    public PersonaModel actualizarPersona(PersonaModel persona, Long id) {

        return personaRepository.findById(id)
                .map(personaActualizado -> {
                    personaActualizado.setNombre(persona.getNombre());
                    personaActualizado.setApellido(persona.getApellido());
                    personaActualizado.setEmail(persona.getEmail());
                    personaActualizado.setTipoDocumento(persona.getTipoDocumento());
                    personaActualizado.setTipoPersona(persona.getTipoPersona());
                    return personaRepository.save(personaActualizado);
                }).orElseThrow(
                        () -> new RuntimeException("Usuario No encontrado con ID: " + id));
    }

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
