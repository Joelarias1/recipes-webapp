package com.example.proyect.repository;

import com.example.proyect.model.RecetaIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaIngredienteRepository extends JpaRepository<RecetaIngrediente, Long> {
    List<RecetaIngrediente> findByRecetaId(Long recetaId);
    List<RecetaIngrediente> findByIngredienteId(Long ingredienteId);
    void deleteByRecetaId(Long recetaId);
} 