package com.example.tutoria1.Exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import com.example.tutoria1.Dto.Exception.ExceptionResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException exception,
            WebRequest request
        ) {

        return buildResponse(
                HttpStatus.BAD_REQUEST, 
                exception.getMessage(), 
                request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponseDTO> handleResponseStatusException(
            ResponseStatusException exception, 
            WebRequest request
        ) {

        HttpStatusCode status = exception.getStatusCode();
        String message = exception.getReason() != null
                ? exception.getReason()
                : "La solicitud no pudo ser procesada";

        return ResponseEntity.status(status).body(
                new ExceptionResponseDTO(
                        LocalDateTime.now(),
                        status.value(),
                        status.toString(),
                        message,
                        request.getDescription(false).replace("uri=", "")
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponseDTO> handleHttpMessageNotReadableException(WebRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es inválido o contiene valores no permitidos",
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDTO> handleUnexpectedException(WebRequest request) {
        
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Intenta nuevamente más tarde",
                request);
    }

    /* CONSTRUCTOR DE RETORNOS -------------------------------------------- */

    private ResponseEntity<ExceptionResponseDTO> buildResponse(
            HttpStatus status,
            String message,
            WebRequest request
        ) {

        ExceptionResponseDTO response = new ExceptionResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(status)
                .body(response);
    }
}
