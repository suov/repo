package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Model.UsuarioModel;

public interface IUsuarioService {

    UsuarioModel crearUsuario(UsuarioModel usuario);

    UsuarioModel actualizarUsuario(UsuarioModel usuario, Integer persona, String login);

    List<UsuarioModel> buscarUsuarios();

}
