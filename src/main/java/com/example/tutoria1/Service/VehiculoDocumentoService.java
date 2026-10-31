package com.example.tutoria1.Service;

import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Service.interfaces.IVehiculoDocumentoService;
import com.example.tutoria1.repository.VehiculoDocumentoRepository;

@Service
public class VehiculoDocumentoService implements IVehiculoDocumentoService {

    private final VehiculoDocumentoRepository vehiculoDocumentoRepository;

    public VehiculoDocumentoService(VehiculoDocumentoRepository vehiculoDocumentoRepository) {
        this.vehiculoDocumentoRepository = vehiculoDocumentoRepository;
    }

    @Override
    public VehiculoDocumentoModel crearVehiculoDocumento(VehiculoDocumentoModel vehiculoDocumento) {
        String archivoBase64 = vehiculoDocumento.getArchivoBase64();
        if (archivoBase64 != null && !archivoBase64.isBlank()) {
            String contenidoBase64 = archivoBase64
                    .replace("data:application/pdf;base64,", "")
                    .replace("data:application/octet-stream;base64,", "")
                    .trim();

            try {
                vehiculoDocumento.setArchivoBase64(contenidoBase64);
                vehiculoDocumento.setArchivoPdf(Base64.getDecoder().decode(contenidoBase64));
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("El archivo PDF no tiene un formato Base64 válido", exception);
            }
        }

        VehiculoDocumentoModel vehiculoDocumentoCreado = vehiculoDocumentoRepository.save(vehiculoDocumento);
        return vehiculoDocumentoCreado;
    };

    @Override
    public List<VehiculoDocumentoModel> listarVehiculoDocumentos() {
        List<VehiculoDocumentoModel> vehiculoDocumento = vehiculoDocumentoRepository.findAll();
        return vehiculoDocumento;
    };

};
