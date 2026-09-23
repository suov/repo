package com.example.tutoria1.repository;

///import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
///import org.springframework.data.jpa.repository.Query;
///import org.springframework.data.repository.query.Param;

import com.example.tutoria1.Model.VehiculoDocumentoModel;

public interface VehiculoDocumentoRepository extends JpaRepository<VehiculoDocumentoModel, Long> {

    /*
     * Función de buscar por nombre de documento - No funciona
     * 
     * @Query("""
     * SELECT DISTINCT vd.vehiculo
     * FROM VehiculoDocumentoModel vd
     * WHERE vd.documento.nombreDocumento = :nombre
     * """)
     * List<VehiculoDocumentoModel> findByNombreDocumento(
     * 
     * @Param("nombre") String nombre);
     */

}
