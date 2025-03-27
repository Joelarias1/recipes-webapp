package com.example.proyect.repository;

import com.example.proyect.model.PasoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasoRecetaRepository extends JpaRepository<PasoReceta, Long> {
    List<PasoReceta> findByRecetaIdOrderByNumeroOrden(Long recetaId);
    void deleteByRecetaId(Long recetaId);
    void deleteByRecetaIdAndId(Long recetaId, Long id);
} 