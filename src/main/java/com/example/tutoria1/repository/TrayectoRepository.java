package com.example.tutoria1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Model.TrayectoModel;

public interface TrayectoRepository extends JpaRepository<TrayectoModel, Long> {

    List<TrayectoModel> findByCodigoRutaOrderByOrdenParadaAsc(
            String codigoRuta);

    List<TrayectoModel> consultarRutaInformacionPorVehiculoYConductor(VehiculoDocumentoStatus statusVehiculo,
            EstadoConductor estadoConductor);
}