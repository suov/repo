package com.example.tutoria1.Dto.Trayecto.Response;

import com.example.tutoria1.Model.PersonaModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RutaConductorResponseDto {
    private String codigoRuta;
    private PersonaModel conductor;

}
