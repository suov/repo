package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Model.UsuarioModel;

public interface IUsuarioService {

    /* UsuarioModel actualizarUsuario(UsuarioModel usuario, Long idPersona, String login); */

    List<UsuarioModel> buscarUsuarios();

    UsuarioModel cambiarContraseña(String login, String nuevaContraseña);

}
