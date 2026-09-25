package com.example.tutoria1.Model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarioPK")
public class UsuarioPKModel implements Serializable {

    @Column(name = "login")
    private String login;

    @Column(name = "persona")
    private Integer persona;
}
