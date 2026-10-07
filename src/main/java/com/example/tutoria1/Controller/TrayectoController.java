package com.example.tutoria1.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Dto.Trayecto.Request.TrayectoRequestDto;
import com.example.tutoria1.Dto.Trayecto.Response.TrayectoResponseDto;
import com.example.tutoria1.Service.interfaces.TrayectoService;

@RestController
@RequestMapping("/api/trayectos")
public class TrayectoController {

    private final TrayectoService trayectoService;

    public TrayectoController(TrayectoService trayectoService) {
        this.trayectoService = trayectoService;
    }

    @PostMapping
    public ResponseEntity<TrayectoResponseDto> crearTrayecto(
            @RequestBody TrayectoRequestDto request,
            Authentication authentication
    ) {

        String loginUsuario = authentication.getName();

        TrayectoResponseDto respuesta = trayectoService.crearTrayecto(
                request,
                loginUsuario
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    @GetMapping("/ruta/{codigoRuta}")
    public ResponseEntity<List<TrayectoResponseDto>> consultarRuta(
            @PathVariable String codigoRuta
    ) {

        return ResponseEntity.ok(
                trayectoService.consultarRutaPorCodigo(codigoRuta)
        );
    }
}