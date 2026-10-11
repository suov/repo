package com.example.tutoria1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.tutoria1.Model.TrayectoModel;

public interface TrayectoRepository extends JpaRepository<TrayectoModel, Long> {

    List<TrayectoModel> findByCodigoRutaOrderByOrdenParadaAsc(
            String codigoRuta);

    @Query("select t from TrayectoModel t, VehiculoPersonaModel vp, VehiculoDocumentoModel vd where vd.estadoDocumento <> HABILITADO OR vp.estadoConductor = RO")
    List<TrayectoModel> consultarRutaPorVehiculoYConductor();
}