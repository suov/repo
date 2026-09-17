package com.example.tutoria1.Dto.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Respuesta para operaciones que necesitan comunicar un mensaje y sus datos. */
@Getter
@AllArgsConstructor
public class ApiResponseDTO<T> {

    private final String message;
    private final T data;
}
