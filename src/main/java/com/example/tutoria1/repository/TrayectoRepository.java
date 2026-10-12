package com.example.tutoria1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tutoria1.Dto.Trayecto.Response.RutaConductorResponseDto;
import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Enums.VehiculoPersona.EstadoConductor;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Model.TrayectoModel;

public interface TrayectoRepository extends JpaRepository<TrayectoModel, Long> {

    List<TrayectoModel> findByCodigoRutaOrderByOrdenParadaAsc(
            String codigoRuta);

    @Query("""
            SELECT DISTINCT t
            FROM TrayectoModel t
            LEFT JOIN t.vehiculo.documentos vd
            LEFT JOIN t.vehiculo.conductores vp
            WHERE (vd.id IS NULL
                OR vd.estadoDocumento <> :habilitado)
            OR (vp.persona.id = t.conductor.id
                AND vp.estadoConductor = :restringido)
            """)
    List<TrayectoModel> consultarRutaPorVehiculoYConductor(
            @Param("habilitado") VehiculoDocumentoStatus habilitado,
            @Param("restringido") EstadoConductor restringido);

    List<TrayectoModel> findByConductor(PersonaModel conductor);

    @Query("""
            SELECT t.codigoRuta, c.id, c.nombre, c.apellido
            FROM TrayectoModel t
            JOIN t.conductor c
            JOIN t.vehiculo v
            WHERE v.placa = :placa;
            """)
    List<RutaConductorResponseDto> consultarCodigoRutaYConductorPorPlaca(@Param("placa") String placa);
}