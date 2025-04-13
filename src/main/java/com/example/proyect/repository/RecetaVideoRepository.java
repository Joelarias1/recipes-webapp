package com.example.proyect.repository;

import com.example.proyect.model.RecetaVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecetaVideoRepository extends JpaRepository<RecetaVideo, Long> {
    
    // Buscar videos por receta
    List<RecetaVideo> findByRecetaId(Long recetaId);
    
    // Buscar videos por título (búsqueda parcial, insensible a mayúsculas/minúsculas)
    List<RecetaVideo> findByTituloContainingIgnoreCase(String titulo);
    
    // Buscar videos por formato
    List<RecetaVideo> findByFormato(String formato);
    
    // Buscar videos por duración (menos de ciertos segundos)
    @Query("SELECT v FROM RecetaVideo v WHERE v.duracionSegundos <= :maxDuracion")
    List<RecetaVideo> findVideosByMaxDuration(@Param("maxDuracion") Integer maxDuracion);
    
    // Contar videos por receta
    Long countByRecetaId(Long recetaId);
    
    // Eliminar videos de una receta
    void deleteByRecetaId(Long recetaId);
} 