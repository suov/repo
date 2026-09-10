package com.example.tutoria1.Model;

import com.example.tutoria1.Enums.Documento.RequisitoSegunVehiculo;
import com.example.tutoria1.Enums.Documento.TipoVehiculo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@Entity 
@Table (name = "documentos")
public class DocumentoModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "codigo_documento_parametrizado", nullable = false)
    private String codigoDocumentoParametrizado;

    @Column (name = "nombre_documento", nullable = false)
    private String nombreDocumento;

    /* A - automovil
     * M - motocicleta
     * AM - ambos */
    @Column (name = "tipo_vehiculo_al_que_aplica", nullable = false)
    private TipoVehiculo tipoVehiculoAlQueAplica;

    /* RA - obligatorio para automovil
     * RM - obligatorio para motocicleta
     * RR - obligatorio para ambos */
    @Column (name = "requisito_segun_tipo_vehiculo", nullable = false)
    private RequisitoSegunVehiculo requisitoSegunTipoVehiculo;

    @Column (name = "descripcion", nullable = false)
    private String descripcion;
}
