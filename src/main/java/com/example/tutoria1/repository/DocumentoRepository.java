package com.example.tutoria1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Enums.Documento.Estado;
import com.example.tutoria1.Enums.Documento.TipoVehiculo;
import com.example.tutoria1.Model.DocumentoModel;

public interface DocumentoRepository extends JpaRepository<DocumentoModel, Long> {
    
    /* Filtrar documentos por tipo de vehículo */
    List<DocumentoModel> findByTipoVehiculoAlQueAplica(TipoVehiculo tipoVehiculo);

    /* Filtrar documentos por estado */
    List<DocumentoModel> findByEstado(Estado estado);

    /* Filtrar documentos por placa */
    List<DocumentoModel> findByPlaca(String placa);
}
