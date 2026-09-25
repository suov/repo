package com.example.tutoria1.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Service.PersonaService;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<PersonaModel>> obtenerPersonas() {
        List<PersonaModel> personas = personaService.listarPersonas();
        return ResponseEntity.ok(personas);
    }

    @PostMapping
    public ResponseEntity<PersonaModel> crearPersona(@RequestBody PersonaModel persona) {
        PersonaModel personaCreada = personaService.crearPersona(persona);
        return ResponseEntity.ok(personaCreada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaModel> actualizarPersona(@PathVariable Long id, @RequestBody PersonaModel persona) {
        try {
            PersonaModel personaActualizada = personaService.actualizarPersona(persona, id);
            return ResponseEntity.ok(personaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
