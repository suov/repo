package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Dto.VehiculoPersona.Request.VehiculoPersonaRequestDto;
import com.example.tutoria1.Dto.VehiculoPersona.Response.VehiculoPersonaResponseDto;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Model.VehiculoModel;

public interface IVehiculoPersonaService {

        VehiculoPersonaResponseDto crearAsociacion(
                        VehiculoPersonaRequestDto requestDto);

        List<VehiculoPersonaResponseDto> listarTodos();

        List<VehiculoPersonaResponseDto> listarPorVehiculo(
                        Long idVehiculo);

        VehiculoPersonaResponseDto actualizarEstado(
                        Long idAsociacion,
                        EstadoConductor estadoConductor);

        void eliminarAsociacion(Long idAsociacion);

        List<VehiculoModel> asociarVehiculosPorCapacidadConductor(Long idConductor);
}