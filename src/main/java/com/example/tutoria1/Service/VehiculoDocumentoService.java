package com.example.tutoria1.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Model.VehiculoModel;
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
        VehiculoDocumentoModel vehiculoDocumentoCreado = vehiculoDocumentoRepository.save(vehiculoDocumento);
        return vehiculoDocumentoCreado;
    };

    @Override
    public List<VehiculoDocumentoModel> listarVehiculoDocumentos() {
        List<VehiculoDocumentoModel> vehiculoDocumento = vehiculoDocumentoRepository.findAll();
        return vehiculoDocumento;
    };

    @Override
    public List<VehiculoModel> buscarVehiculoPorEstadoDocumento(VehiculoDocumentoStatus estado) {
        return vehiculoDocumentoRepository.buscarVehiculoPorEstadoDocumento(estado);
    }
};
