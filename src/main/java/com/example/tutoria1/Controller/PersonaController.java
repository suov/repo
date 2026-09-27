package com.example.tutoria1.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.tutoria1.Dto.Exception.ApiResponseDTO;
import com.example.tutoria1.Dto.Persona.Request.PersonaRequestDto;
import com.example.tutoria1.Dto.Persona.Response.PersonaResponseDto;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Service.PersonaService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/persona")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    /* OK */
    @GetMapping
    public List<PersonaResponseDto> obtenerPersonas() {

        List<PersonaModel> personas = personaService.listarPersonas();

        if(personas.isEmpty()) {
            throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encuentran personas que mostrar");
        }

        return personas.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /* OK */
    @PostMapping
    public ResponseEntity<Object> crearPersona(
        @RequestBody PersonaRequestDto dto
    ) {

        PersonaModel persona = convertirAModel(dto);
        PersonaModel personaCreada = personaService.crearPersona(persona);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                    new ApiResponseDTO<>(
                        "Persona creada correctamente",
                        convertirAResponse(personaCreada)
                    )
                );
    }

    /* OK */
    @PutMapping("/{id}")
    public PersonaResponseDto actualizarPersona(
        @PathVariable Long id, 
        @RequestBody PersonaRequestDto personaRequest
    ) {

        PersonaModel persona = convertirAModel(personaRequest);
        PersonaModel personaActualizada = personaService.actualizarPersona(persona, id);
        return convertirAResponse(personaActualizada);
    }


    /* MAPPERS */
    private PersonaModel convertirAModel(PersonaRequestDto dto) {
        PersonaModel persona = new PersonaModel();

        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setDocumento(dto.getDocumento());
        persona.setTipoDocumento(dto.getTipoDocumento());
        persona.setEmail(dto.getEmail());
        persona.setTipoPersona(dto.getTipoPersona());
        return persona;
    }

    private PersonaResponseDto convertirAResponse(PersonaModel persona) {
        
        if (persona == null) {
            return null;
        }

        PersonaResponseDto dto = new PersonaResponseDto();
        dto.setId(persona.getId());
        dto.setNombre(persona.getNombre());
        dto.setApellido(persona.getApellido());
        dto.setEmail(persona.getEmail());
        dto.setTipoPersona(persona.getTipoPersona());
        dto.setTipoDocumento(persona.getTipoDocumento());
        dto.setDocumento(persona.getDocumento());
        return dto;
    }
}
