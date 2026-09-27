package com.example.tutoria1.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Dto.Exception.ApiResponseDTO;
import com.example.tutoria1.Dto.Usuario.Request.UsuarioRequestDto;
import com.example.tutoria1.Dto.Usuario.Response.UsuarioResponseDto;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Model.UsuarioModel;
import com.example.tutoria1.Model.UsuarioPKModel;
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

        return usuarios.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Object> crearUsuario(
            @RequestBody UsuarioRequestDto dto
    ) {

        UsuarioModel usuario = convertirAModel(dto);
        UsuarioModel usuarioCreado = usuarioService.crearUsuario(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponseDTO<>(
                                "Usuario creado correctamente",
                                convertirAResponse(usuarioCreado)
                        )
                );
    }

    @PutMapping("/{idPersona}/{login}")
    public UsuarioResponseDto actualizarUsuario(
            @PathVariable Long idPersona,
            @PathVariable String login,
            @RequestBody UsuarioRequestDto usuarioRequest
    ) {

        UsuarioModel usuario = convertirAModel(usuarioRequest);
        UsuarioModel usuarioActualizado = usuarioService.actualizarUsuario(usuario, idPersona, login);
        return convertirAResponse(usuarioActualizado);
    }

    /* MAPPERS */
    private UsuarioModel convertirAModel(UsuarioRequestDto dto) {
        UsuarioModel usuario = new UsuarioModel();

        UsuarioPKModel usuarioPK = new UsuarioPKModel();
        usuarioPK.setLogin(dto.getLogin());
        usuarioPK.setIdpersona(dto.getIdpersona());
        usuario.setUsuarioPK(usuarioPK);

        PersonaModel persona = new PersonaModel();
        persona.setId(dto.getIdpersona());
        usuario.setPersona(persona);

        usuario.setPassword(dto.getPassword());
        return usuario;
    }

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
