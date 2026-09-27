package com.example.tutoria1.Dto.VehiculoDocumento.Request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculoDocumentoRequestDto {

    private Long documentoId;
    private String nombreArchivo;
    private String archivoBase64;
    private LocalDate fechaExpedicion;
    private LocalDate fechaVencimiento;
}
