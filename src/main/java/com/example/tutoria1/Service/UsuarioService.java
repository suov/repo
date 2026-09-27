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
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<UsuarioModel> buscarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public UsuarioModel actualizarUsuario(
        UsuarioModel usuario,
        Long idPersona, String login
    ) {

        if(usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        if(idPersona == null || login == null || login.isBlank()) {
            throw new IllegalArgumentException("La persona y el login son obligatorios");
        }

        UsuarioPKModel usuarioPk = new UsuarioPKModel();
        usuarioPk.setLogin(login);
        usuarioPk.setIdpersona(idPersona);

        return usuarioRepository.findById(usuarioPk)
                .map(usuarioExistente -> {
                    //Solo se actualiza la contraseña
                    if(usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
                        usuarioExistente.setPassword(usuario.getPassword());
                    }

                    /* No actualizamos Apikey */
                    return usuarioRepository.save(usuarioExistente);
                }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

}
