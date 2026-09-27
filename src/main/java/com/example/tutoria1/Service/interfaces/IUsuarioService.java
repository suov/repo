package com.example.tutoria1.Service.interfaces;

import java.util.List;

import com.example.tutoria1.Model.UsuarioModel;

public interface IUsuarioService {

    List<UsuarioModel> buscarUsuarios();

    UsuarioModel cambiarContraseña(String login, String nuevaContraseña);
}
