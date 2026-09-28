package com.example.tutoria1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Model.VehiculoModel;
import com.example.tutoria1.Model.VehiculoPersonaModel;

public interface VehiculoPersonaRepository
                extends JpaRepository<VehiculoPersonaModel, Long> {

        long countByVehiculoId(Long idVehiculo);

        boolean existsByVehiculoIdAndPersonaId(
                        Long idVehiculo,
                        Long idPersona);

        List<VehiculoPersonaModel> findByVehiculoId(Long idVehiculo);

        @Query("select v.*" +
                        "from vehiculo_persona vp" +
                        "INNER JOIN vehiculo v ON vp.id_vehiculo = v.id" +
                        "where vp.estado_conductor=PO AND vp.id_persona=:id_conductor")
        List<VehiculoModel> asociarVehiculosPorCapacidadConductor(
                        @Param("id_conductor") Long idConductor);
}