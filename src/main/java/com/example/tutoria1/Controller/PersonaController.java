package com.example.tutoria1.Controller;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.tutoria1.Dto.Exception.ApiResponseDTO;
import com.example.tutoria1.Dto.Persona.Request.PersonaRequestDto;
import com.example.tutoria1.Dto.Persona.Response.PersonaResponseDto;
import com.example.tutoria1.Model.PersonaModel;
import com.example.tutoria1.Service.PersonaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/persona")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    /* OK */
    @Operation(
        summary = "Listar todas las personas",
        description = "Obtiene la información de todas las personas registradas en el sistema. Si no existen registros, se genera una respuesta de recurso no encontrado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Listado de personas obtenido correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PersonaResponseDto.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "No se encuentran personas registradas para mostrar", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al consultar las personas", content = @Content)
    })
    @GetMapping
    public List<PersonaResponseDto> obtenerPersonas() {

        List<PersonaModel> personas = personaService.listarPersonas();

        if(personas.isEmpty()) {
            throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encuentran personas que mostrar");
        }

        return personas.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /* OK */
    @Operation(
        summary = "Registrar una persona",
        description = "Registra una nueva persona en el sistema con sus datos personales, información de contacto, tipo de persona y, cuando corresponda, información de la licencia de conducción."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Persona creada correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Los datos enviados no son válidos o la licencia no contiene un Base64 válido", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al registrar la persona", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Object> crearPersona(
        @RequestBody PersonaRequestDto dto
    ) {

        PersonaModel persona = convertirAModel(dto);
        PersonaModel personaCreada = personaService.crearPersona(persona);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                    new ApiResponseDTO<>(
                        "Persona creada correctamente",
                        convertirAResponse(personaCreada)
                    )
                );
    }

    /* OK */
    @Operation(
        summary = "Actualizar una persona",
        description = "Actualiza los datos de una persona existente utilizando su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Persona actualizada correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PersonaResponseDto.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Los datos enviados no son válidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "No se encontró la persona que se desea actualizar", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al actualizar la persona", content = @Content)
    })
    @PutMapping("/{id}")
    public PersonaResponseDto actualizarPersona(
        @Parameter(description = "Identificador único de la persona que se desea actualizar", example = "1")
        @PathVariable Long id,
        @RequestBody PersonaRequestDto personaRequest
    ) {

        PersonaModel persona = convertirAModel(personaRequest);
        PersonaModel personaActualizada = personaService.actualizarPersona(persona, id);
        return convertirAResponse(personaActualizada);
    }

    /* TUTORÍA 2 */
    @Operation(
        summary = "Consultar total de personas por tipo",
        description = "Agrupa las personas registradas según su tipo y devuelve la cantidad de registros correspondiente a cada categoría. Las personas sin tipo asignado se agrupan bajo la categoría SIN_TIPO."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Totales de personas agrupados por tipo obtenidos correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "object",
                    example = "{\"C\": 10, \"A\": 5, \"SIN_TIPO\": 2}"
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno al calcular los totales de personas", content = @Content)
    })
    @GetMapping("/total-por-tipo")
    public Map<String, Long> totalPersonasPorTipo() {

        return personaService.listarPersonas().stream()
                .collect(Collectors.groupingBy(
                        persona -> persona.getTipoPersona() == null ? "SIN_TIPO" : persona.getTipoPersona().name(),
                        Collectors.counting()
                ));
    }

    /* MAPPERS */
    private PersonaModel convertirAModel(PersonaRequestDto dto) {
        PersonaModel persona = new PersonaModel();

        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setDocumento(dto.getDocumento());
        persona.setTipoDocumento(dto.getTipoDocumento());
        persona.setEmail(dto.getEmail());
        persona.setTipoPersona(dto.getTipoPersona());

        if (dto.getLicenciaConduccionBase64() != null
                && !dto.getLicenciaConduccionBase64().isBlank()) {
            try {
                persona.setLicenciaConduccion(
                        Base64.getDecoder().decode(dto.getLicenciaConduccionBase64().trim())
                );
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        "La licencia de conducción no contiene un Base64 válido"
                );
            }
        }
        persona.setFechaVigenciaLicencia(dto.getFechaVigenciaLicencia());
        return persona;
    }

    private PersonaResponseDto convertirAResponse(PersonaModel persona) {

        if (persona == null) {
            return null;
        }

        PersonaResponseDto dto = new PersonaResponseDto();
        dto.setId(persona.getId());
        dto.setNombre(persona.getNombre());
        dto.setApellido(persona.getApellido());
        dto.setEmail(persona.getEmail());
        dto.setTipoPersona(persona.getTipoPersona());
        dto.setTipoDocumento(persona.getTipoDocumento());
        dto.setDocumento(persona.getDocumento());

        if (persona.getLicenciaConduccion() != null) {
            dto.setLicenciaConduccionBase64(
                    Base64.getEncoder().encodeToString(persona.getLicenciaConduccion())
            );
        }

        dto.setFechaVigenciaLicencia(persona.getFechaVigenciaLicencia());
        return dto;
    }
}