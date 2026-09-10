package com.example.tutoria1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutoria1.Model.DocumentoModel;

public interface DocumentoRepository extends JpaRepository<DocumentoModel, Long> {
    
}
