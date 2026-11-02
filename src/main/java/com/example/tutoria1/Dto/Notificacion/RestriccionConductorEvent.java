package com.example.tutoria1.Dto.Notificacion;

import java.util.List;

public record RestriccionConductorEvent(
        Long conductorId,
        String nombreConductor,
        String correo,
        List<String> placas,
        List<String> rutas,
        String fechaVencimientoLicencia) {
}
