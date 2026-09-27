package com.example.tutoria1.Dto.VehiculoPersona.Response;

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
public class VehiculoPersonaResponseDto {

    private Long id;

    private Long idVehiculo;

    private String placaVehiculo;

    private Long idPersona;

    private String nombreConductor;

    private LocalDate fechaAsociacion;

    private EstadoConductor estadoConductor;
}