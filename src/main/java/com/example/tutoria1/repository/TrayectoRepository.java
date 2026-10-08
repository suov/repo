package com.example.tutoria1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Model.TrayectoModel;

public interface TrayectoRepository extends JpaRepository<TrayectoModel, Long> {

    List<TrayectoModel> findByCodigoRutaOrderByOrdenParadaAsc(
            String codigoRuta
    );
}