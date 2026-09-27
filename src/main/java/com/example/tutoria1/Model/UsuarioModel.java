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

    /* Se ejecuta justo antes de guardar el objeto en la base de datos asegurando que la información esté completa*/
    @PrePersist
    public void generarDatosAutomaticos() {

        /* VALIDACIONES ----------------------- */
        if (this.persona == null) {
            throw new IllegalStateException("La persona es obligatoria para crear un usuario.");
        }

        if (this.persona.getTipoPersona() != TipoPersona.A) {
            throw new IllegalStateException("Solo las personas administrativas pueden tener usuario.");
        }
        /* ------------------------------------- */

        if (this.usuarioPK == null) {
            this.usuarioPK = new UsuarioPKModel();
        }

        if (this.usuarioPK.getIdpersona() == null) {
            this.usuarioPK.setIdpersona(this.persona.getId());
        }

        if (this.usuarioPK.getLogin() == null || this.usuarioPK.getLogin().isBlank()) {
            this.usuarioPK.setLogin(generarLogin(this.persona));
        }

        if (this.apikey == null || this.apikey.isBlank()) {
            this.apikey = UUID.randomUUID().toString();
        }
    }

    private String generarLogin(PersonaModel persona) {
        if (persona == null) {
            throw new IllegalStateException("La persona es obligatoria para crear el usuario.");
        }

        String nombre = persona.getNombre();
        String apellido = persona.getApellido();
        String documento = persona.getDocumento();

        if (nombre == null 
            || nombre.isBlank() 
            || apellido == null 
            || apellido.isBlank() 
            || documento == null 
            || documento.isBlank()) {
            throw new IllegalStateException("Falta información de la persona para generar el login.");
        }

        /* Nemotecnia */
        String primeraLetraNombre = nombre.substring(0, 1).toUpperCase();
        String primeraLetraApellido = apellido.substring(0, 1).toUpperCase();

        return primeraLetraNombre + primeraLetraApellido + documento.trim();
    }
}
