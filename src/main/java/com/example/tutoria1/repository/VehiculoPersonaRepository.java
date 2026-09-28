package com.example.tutoria1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Model.VehiculoPersonaModel;

public interface VehiculoPersonaRepository
        extends JpaRepository<VehiculoPersonaModel, Long> {

    long countByVehiculoId(Long idVehiculo);

    boolean existsByVehiculoIdAndPersonaId(
            Long idVehiculo,
            Long idPersona
    );

    List<VehiculoPersonaModel> findByVehiculoId(Long idVehiculo);
}