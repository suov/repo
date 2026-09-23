package com.example.tutoria1.Dto.Exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponseDTO {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
}
