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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
        summary = "Listar usuarios",
        description = "Obtiene la lista de usuarios registrados en el sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios obtenida correctamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation = UsuarioResponseDto.class)
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encuentran usuarios para mostrar",
            content = @Content
        )
    })
    @GetMapping
    public List<UsuarioResponseDto> obtenerUsuarios() {

        List<UsuarioModel> usuarios = usuarioService.buscarUsuarios();

        if (usuarios.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No se encuentran usuarios para mostrar"
            );
        }

        return usuarios.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Operation(
        summary = "Cambiar contraseña de un usuario",
        description = "Actualiza la contraseña del usuario identificado por su login. " +
                      "El cuerpo de la solicitud debe contener la propiedad 'contraseña'."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contraseña actualizada correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "El login o la nueva contraseña son obligatorios",
            content = @Content
        )
    })
    @PutMapping("/{login}/password")
    public ResponseEntity<Object> cambiarContraseña(
            @Parameter(
                description = "Login del usuario al que se le actualizará la contraseña",
                example = "jzapata"
            )
            @PathVariable String login,

            @RequestBody
            Map<String, String> request
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

    @Operation(
        summary = "Regenerar API Key",
        description = "Genera una nueva API Key para el usuario identificado por su login."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "API Key regenerada correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)
            )
        )
    })
    @GetMapping("/{login}/api-key/regenerar")
    public ResponseEntity<Object> regenerarApiKey(
            @Parameter(
                description = "Login del usuario al que se le regenerará la API Key",
                example = "jzapata"
            )
            @PathVariable String login
    ) {

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