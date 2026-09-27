package com.example.tutoria1.Model;

import java.util.UUID;

import com.example.tutoria1.Enums.Persona.TipoPersona;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "usuario")
public class UsuarioModel {

    /* Login */
    @EmbeddedId
    private UsuarioPKModel usuarioPK;

    /* ID Persona */
    @MapsId("idpersona")
    @OneToOne(optional = false)
    @JoinColumn(name = "idpersona", referencedColumnName = "id", nullable = false, unique = true)
    private PersonaModel persona;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "apikey", nullable = false, unique = true, updatable = false)
    private String apikey;
}
