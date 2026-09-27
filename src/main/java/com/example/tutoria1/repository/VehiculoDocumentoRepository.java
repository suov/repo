package com.example.tutoria1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Model.VehiculoDocumentoModel;

public interface VehiculoDocumentoRepository extends JpaRepository<VehiculoDocumentoModel, Long> {

    Optional<VehiculoDocumentoModel> findByVehiculoIdAndDocumentoId(Long vehiculoId, Long documentoId);

}
