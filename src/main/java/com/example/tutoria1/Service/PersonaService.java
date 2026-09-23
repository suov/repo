package com.example.tutoria1.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Service.interfaces.IPersonaService;
import com.example.tutoria1.repository.PersonaRepository;

@Service
public class PersonaService implements IPersonaService {
    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    };

    @Override
    public PersonaModel crearPersona(PersonaModel persona) {
        PersonaModel personaCreada = personaRepository.save(persona);
        return personaCreada;
    };

    @Override
    public List<PersonaModel> listarPersonas() {
        List<PersonaModel> personas = personaRepository.findAll();
        return personas;
    };

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
}
