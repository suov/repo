package com.example.tutoria1.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.tutoria1.Model.UsuarioModel;
import com.example.tutoria1.Service.interfaces.IUsuarioService;
import com.example.tutoria1.repository.UsuarioRepository;

@Service
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<UsuarioModel> buscarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public UsuarioModel cambiarContraseña(String login, String nuevaPassword) {

        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("El login es obligatorio");
        }

        if (nuevaPassword == null || nuevaPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        UsuarioModel usuario = usuarioRepository.findByUsuarioPKLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPassword(nuevaPassword);
        return usuarioRepository.save(usuario);
    }

    public UsuarioModel regenerarApiKey(String login) {
        
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("El login es obligatorio");
        }

        UsuarioModel usuario = usuarioRepository.findByUsuarioPKLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setApikey(UUID.randomUUID().toString());
        return usuarioRepository.save(usuario);
    }

}
