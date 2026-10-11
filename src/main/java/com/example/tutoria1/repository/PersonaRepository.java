package com.example.tutoria1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Model.PersonaModel;
import java.util.List;


public interface PersonaRepository extends JpaRepository<PersonaModel, Long> {

    List<PersonaModel> findByDocumento(String documento);
}
