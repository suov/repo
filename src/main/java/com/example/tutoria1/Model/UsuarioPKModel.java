package com.example.tutoria1.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPKModel {

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "idpersona", nullable = false)
    private Long idpersona;
}