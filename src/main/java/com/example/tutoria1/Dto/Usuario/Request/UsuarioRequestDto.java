package com.example.tutoria1.Dto.Usuario.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequestDto {

    private String login;
    private Long idpersona;
    private String password;
}
