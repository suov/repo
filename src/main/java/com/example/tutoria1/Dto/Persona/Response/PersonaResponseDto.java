package com.example.tutoria1.Dto.Persona.Response;

import com.example.tutoria1.Enums.Persona.TipoDocumento;
import com.example.tutoria1.Enums.Persona.TipoPersona;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaResponseDto {

    private Long id;
    private String documento;
    private TipoDocumento tipoDocumento;
    private String nombre;
    private String apellido;
    private String email;
    private TipoPersona tipoPersona;
}
