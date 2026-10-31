package com.example.tutoria1.Dto.Trayecto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RutaConductorResponseDto {

    private String codigoRuta;
    private Long conductorId;
    private String nombreConductor;
    private String documentoConductor;
}