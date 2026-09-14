package com.example.tutoria1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Model.VehiculoModel;

import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<VehiculoModel, Long> {

    boolean existsByPlaca(String placa);

    Optional<VehiculoModel> findByPlaca(String placa);
}