package com.example.tutoria1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling 
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class Tutoria1Application {

	public static void main(String[] args) {
		SpringApplication.run(Tutoria1Application.class, args);
	}

}
