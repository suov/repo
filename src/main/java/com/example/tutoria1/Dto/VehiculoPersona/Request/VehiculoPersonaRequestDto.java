package com.example.tutoria1.Dto.VehiculoPersona.Request;

import java.time.LocalDate;

import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoPersonaRequestDto {

    private Long idVehiculo;

    private Long idPersona;

    private LocalDate fechaAsociacion;

    private EstadoConductor estadoConductor;
}