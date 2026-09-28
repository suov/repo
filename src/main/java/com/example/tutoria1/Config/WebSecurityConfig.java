package com.example.tutoria1.Config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.tutoria1.Enums.Persona.TipoPersona;
import com.example.tutoria1.Model.UsuarioModel;
import com.example.tutoria1.repository.UsuarioRepository;


@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
	
	final JWTAuthorizationFilter jwtAuthorizationFilter;

	WebSecurityConfig(JWTAuthorizationFilter jwtAuthorizationFilter) {
		this.jwtAuthorizationFilter = jwtAuthorizationFilter;
	}
	
	@Bean
	public UserDetailsService jwtInMemoryUserDetailsService(UsuarioRepository usuarioRepository) {

		return username -> {
			UsuarioModel usuario = usuarioRepository.findByUsuarioPKLogin(username)
					.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
					
			String role = usuario.getPersona() != null && usuario.getPersona().getTipoPersona() == TipoPersona.A
					? "ROLE_ADMIN"
					: "ROLE_USER";

			UserDetails userDetails = User
					.withUsername(usuario.getUsuarioPK().getLogin())
					.password(usuario.getPassword())
					.authorities(List.of(new SimpleGrantedAuthority(role)))
					.build();

			return userDetails;
		};
	}
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {

		http
			.csrf((csrf) -> csrf.disable())
			.authorizeHttpRequests(authz -> authz
					.requestMatchers(Constans.LOGIN_URL).permitAll() //OK
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.requestMatchers("/persona/total-por-tipo").permitAll() //OK
					.requestMatchers("/api/vehiculos/estadoDocumento/**").permitAll() //OK
					.requestMatchers("/api/vehiculos/placa/**").permitAll() //OK
					.requestMatchers("/api/vehiculos/por-vencer/**").permitAll() //OK
					.requestMatchers("/api/vehiculos/vencidos").permitAll() //OK
					.requestMatchers("/api/vehiculo-persona/conductores/pueden-operar").permitAll() //OK
					.requestMatchers("/persona/**").hasRole("ADMIN") //OK
					.requestMatchers("/usuario/**").hasRole("ADMIN") //OK
					.requestMatchers("/api/vehiculos/**").hasRole("ADMIN") //OK
					.requestMatchers("/api/vehiculo-persona/**").hasRole("ADMIN") //OK
					.anyRequest().authenticated())
			.addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
