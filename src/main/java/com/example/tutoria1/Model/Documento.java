package com.example.tutoria1.Model;

import com.example.tutoria1.Model.enums.Documento_Estado;
import com.example.tutoria1.Model.enums.Documento_RequisitoSegunVehiculo;
import com.example.tutoria1.Model.enums.Documento_TipoVehiculo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@Entity 
@Table (name = "documentos")
public class Documento {
    
    @Id
    private Long id;

    @Column (name = "codigo_documento_parametrizado", nullable = false)
    private String codigoDocumentoParametrizado;

    @Column (name = "nombre_documento", nullable = false)
    private String nombreDocumento;

    /* A - automovil
     * M - motocicleta
     * AM - ambos */
    @Column (name = "tipo_vehiculo_al_que_aplica", nullable = false)
    private Documento_TipoVehiculo tipoVehiculoAlQueAplica;

    /* RA - obligatorio para automovil
     * RM - obligatorio para motocicleta
     * RR - obligatorio para ambos */
    @Column (name = "requisito_segun_tipo_vehiculo", nullable = false)
    private Documento_RequisitoSegunVehiculo requisitoSegunTipoVehiculo;

    @Column (name = "descripcion", nullable = false)
    private String descripcion;

    @Column (name = "estado", nullable = false)
    private Documento_Estado estado;

    /* Espacio para el ManyToOne de la relación con vehiculos */
}
