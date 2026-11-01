package com.example.tutoria1.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tutoria1.Service.ExcelService;

@RestController
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500"
})
@RequestMapping("/api/trayectos")
public class ExcelController {

    private final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping(
            value = "/cargar-excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> cargarExcel(
            @RequestParam("archivo") MultipartFile archivo,
            Authentication authentication) {

        if (archivo.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("El archivo Excel está vacío.");
        }

        String loginUsuario = authentication.getName();

        excelService.processExcel(archivo, loginUsuario);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("El archivo Excel fue recibido y procesado.");
    }
}