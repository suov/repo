package com.example.tutoria1.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.tutoria1.Dto.Exception.ApiResponseDTO;
import com.example.tutoria1.Dto.Usuario.Response.UsuarioResponseDto;
import com.example.tutoria1.Model.UsuarioModel;
import com.example.tutoria1.Service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponseDto> obtenerUsuarios() {

        List<UsuarioModel> usuarios = usuarioService.buscarUsuarios();

        if(usuarios.isEmpty()) {
            throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encuentran usuarios para mostrar");
        }

        return usuarios.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @PutMapping("/{login}/password")
    public ResponseEntity<Object> cambiarContraseña(
            @PathVariable String login,
            @RequestBody Map<String, String> request
    ) {

        String nuevaContraseña = request.get("contraseña");

        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("El login es obligatorio");
        }

        if (nuevaContraseña == null || nuevaContraseña.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña es obligatoria");
        }

        UsuarioModel usuario = usuarioService.cambiarContraseña(login, nuevaContraseña);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(
                    "Contraseña actualizada correctamente", 
                    usuario
                )
        );
    }

    /* TUTORÍA 2 */
    @GetMapping("/{login}/api-key/regenerar")
    public ResponseEntity<Object> regenerarApiKey(@PathVariable String login) {

        UsuarioModel usuario = usuarioService.regenerarApiKey(login);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(
                    "APIKey regenerado correctamente", 
                    convertirAResponse(usuario)
                )
        );
    }

    /* MAPPERS */
    private UsuarioResponseDto convertirAResponse(UsuarioModel usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioResponseDto dto = new UsuarioResponseDto();

        if (usuario.getUsuarioPK() != null) {
            dto.setLogin(usuario.getUsuarioPK().getLogin());
            dto.setIdpersona(usuario.getUsuarioPK().getIdpersona());
        }

        dto.setApikey(usuario.getApikey());
        return dto;
    }
}
