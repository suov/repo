package com.example.tutoria1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Model.UsuarioModel;
import com.example.tutoria1.Model.UsuarioPKModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, UsuarioPKModel> {

}
