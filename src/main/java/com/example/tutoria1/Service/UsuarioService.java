package com.example.tutoria1.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tutoria1.Model.UsuarioModel;
import com.example.tutoria1.Model.UsuarioPKModel;
import com.example.tutoria1.Service.interfaces.IUsuarioService;
import com.example.tutoria1.repository.UsuarioRepository;

@Service
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioModel crearUsuario(UsuarioModel usuario) {
        UsuarioModel nuevoUsuario = usuarioRepository.save(usuario);
        return nuevoUsuario;
    }

    @Override
    public List<UsuarioModel> buscarUsuarios() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        return usuarios;
    }

    @Override
    public UsuarioModel actualizarUsuario(UsuarioModel usuario, Integer persona, String login) {

        // Creación de usuarioPK como ID
        UsuarioPKModel usuarioPk = new UsuarioPKModel();
        usuarioPk.setLogin(login);
        usuarioPk.setPersona(persona);

        // Actualización Usuario
        return usuarioRepository.findById(usuarioPk)
                .map(usuarioEncontrado -> {
                    usuarioEncontrado.setContrasena(usuario.getContrasena());
                    usuarioEncontrado.setApiKey(usuario.getApiKey());
                    return usuarioRepository.save(usuarioEncontrado);
                }).orElseThrow(
                        () -> new RuntimeException("Usuario No encontrado"));
    }

}
