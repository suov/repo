package com.example.tutoria1.Model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class UsuarioModel {

    @EmbeddedId
    private UsuarioPKModel id;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "apikey", nullable = false)
    private String apiKey;

}
