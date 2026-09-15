package com.example.tutoria1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tutoria1.Enums.VehiculoDocumento.VehiculoDocumentoStatus;
import com.example.tutoria1.Model.VehiculoDocumentoModel;
import com.example.tutoria1.Model.VehiculoModel;

public interface VehiculoDocumentoRepository extends JpaRepository<VehiculoDocumentoModel, Long> {

    @Query("""
            SELECT DISTINCT vd.vehiculo
            FROM VehiculoDocumentoModel vd
            WHERE vd.estadoDocumento = :estado
            """)
    List<VehiculoModel> buscarVehiculoPorEstadoDocumento(@Param("estado") VehiculoDocumentoStatus estado);

}
