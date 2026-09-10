package com.example.tutoria1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Enums.Documento.TipoVehiculo;
import com.example.tutoria1.Model.DocumentoModel;

public interface DocumentoRepository extends JpaRepository<DocumentoModel, Long> {
    List<DocumentoModel> findByTipoVehiculoAlQueAplica(TipoVehiculo tipoVehiculo);
}
