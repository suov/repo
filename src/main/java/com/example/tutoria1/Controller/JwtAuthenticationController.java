package com.example.tutoria1.Controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutoria1.Config.JWTAuthenticationConfig;
import com.example.tutoria1.Dto.Jwt.JwtRequest;
import com.example.tutoria1.Dto.Jwt.JwtResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    private final JWTAuthenticationConfig jwtAuthenticationConfig;
    private final UserDetailsService jwtInMemoryUserDetailsService;

    JwtAuthenticationController(
        JWTAuthenticationConfig jwtAuthenticationConfig,
        UserDetailsService jwtInMemoryUserDetailsService
    ) {
        this.jwtAuthenticationConfig = jwtAuthenticationConfig;
        this.jwtInMemoryUserDetailsService = jwtInMemoryUserDetailsService;
    }

    @Operation(
        summary = "Autenticar usuario y generar token JWT",
        description = "Valida las credenciales de acceso del usuario. Si son correctas, genera y devuelve un token JWT que deberá utilizarse para autenticarse en los endpoints protegidos de la API."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autenticación exitosa. Se devuelve el token JWT generado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JwtResponse.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "La solicitud contiene datos de autenticación incompletos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Las credenciales proporcionadas son incorrectas", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno durante el proceso de autenticación", content = @Content)
    })
    @RequestMapping(
        value = "/authenticate",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createAuthenticationToken(
        @RequestBody JwtRequest authenticationRequest
    ) throws Exception {

        if (authenticationRequest == null
            || authenticationRequest.getUsername() == null
            || authenticationRequest.getPassword() == null) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        final UserDetails userDetails = jwtInMemoryUserDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        if (!userDetails.getPassword().equals(authenticationRequest.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        final String authority = userDetails.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .orElse("ROLE_USER");

        final String token = jwtAuthenticationConfig.getJWTToken(
            userDetails.getUsername(),
            authority
        );

        return ResponseEntity.ok(new JwtResponse(token));
    }
}