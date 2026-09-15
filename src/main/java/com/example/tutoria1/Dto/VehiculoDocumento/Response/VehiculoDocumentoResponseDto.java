package com.example.tutoria1.Dto.VehiculoDocumento.Response;

import java.time.LocalDate;

import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculoDocumentoResponseDto {

    private Long id;
    private Long documentoId;
    private LocalDate fechaExpedicion;
    private LocalDate fechaVencimiento;
    private VehiculoDocumentoStatus estadoDocumento;
}
