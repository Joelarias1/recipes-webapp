package com.example.proyect.repository;

import com.example.proyect.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByNombreContainingIgnoreCaseAndPublicaTrue(String nombre);
    
    List<Receta> findByPublicaTrue();
    
    List<Receta> findByDificultadAndPublicaTrue(String dificultad);
    
    @Query("SELECT r FROM Receta r WHERE r.publica = true ORDER BY r.fechaCreacion DESC")
    List<Receta> findRecentPublicRecipes();
    
    List<Receta> findByCreadorId(Long creadorId);
} 