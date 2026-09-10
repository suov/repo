package com.example.tutoria1.repository;

import com.example.tutoria1.Model.Vehiculo.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    boolean existsByPlaca(String placa);

    Optional<Vehiculo> findByPlaca(String placa);
}