package com.example.tutoria1.Model;

import com.example.tutoria1.Enums.Documento.RequisitoSegunVehiculo;
import com.example.tutoria1.Enums.Documento.TipoVehiculo;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documentos", check = {
        /* Condiciones de los atributos de documento */
        @CheckConstraint(name = "chk_tipo_vehiculo_al_que_aplica", constraint = "tipo_vehiculo_al_que_aplica IN IN ('A', 'M', 'AM')"),
        @CheckConstraint(name = "chk_requisito_segun_tipo_vehiculo", constraint = "requisito_segun_tipo_vehiculo IN IN ('RA', 'RM', 'RR')")
})
public class DocumentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_documento_parametrizado", nullable = false)
    private String codigoDocumentoParametrizado;

    @Column(name = "nombre_documento", nullable = false)
    private String nombreDocumento;

    /*
     * A - automovil
     * M - motocicleta
     * AM - ambos
     */
    @Column(name = "tipo_vehiculo_al_que_aplica", nullable = false)
    private TipoVehiculo tipoVehiculoAlQueAplica;

    /*
     * RA - obligatorio para automovil
     * RM - obligatorio para motocicleta
     * RR - obligatorio para ambos
     */
    @Column(name = "requisito_segun_tipo_vehiculo", nullable = false)
    private RequisitoSegunVehiculo requisitoSegunTipoVehiculo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;
}
