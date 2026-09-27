package com.example.tutoria1.Model;

import com.example.tutoria1.Enums.Persona.TipoDocumento;
import com.example.tutoria1.Enums.Persona.TipoPersona;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "persona", 
    check = {
        @CheckConstraint(
            name = "chk_tipo_persona", 
            constraint = "tipo_persona IN('C', 'A')"
        ),
        @CheckConstraint(
            name = "chk_tipo_documento", 
            constraint = "tipo_documento IN('CC')"
        )
    })
public class PersonaModel {

    //Identificador de persona (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Identificación de la persona
    @Column(name = "numero_documento", unique = true, nullable = false)
    private String documento;

    //Tipo de identificación - CC
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /* C- Conductor
    ** A- Administrativo */
    @Column(name = "tipo_persona")
    @Enumerated(EnumType.STRING)
    private TipoPersona tipoPersona;

}
