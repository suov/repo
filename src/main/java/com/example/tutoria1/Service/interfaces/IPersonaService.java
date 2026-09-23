package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Model.PersonaModel;

public interface IPersonaService {

    public PersonaModel crearPersona(PersonaModel persona);

    public List<PersonaModel> listarPersonas();

    public PersonaModel actualizarPersona(PersonaModel persona, Long id);

}
