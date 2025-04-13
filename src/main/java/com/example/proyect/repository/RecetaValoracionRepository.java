package com.example.proyect.repository;

import com.example.proyect.model.RecetaValoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecetaValoracionRepository extends JpaRepository<RecetaValoracion, Long> {
    
    // Buscar valoraciones por receta
    List<RecetaValoracion> findByRecetaId(Long recetaId);
    
    // Buscar valoraciones por usuario
    List<RecetaValoracion> findByUsuarioId(Long usuarioId);
    
    // Buscar valoración específica de un usuario para una receta
    Optional<RecetaValoracion> findByRecetaIdAndUsuarioId(Long recetaId, Long usuarioId);
    
    // Calcular la puntuación promedio de una receta
    @Query("SELECT AVG(rv.puntuacion) FROM RecetaValoracion rv WHERE rv.receta.id = :recetaId")
    Double calcularPuntuacionPromedio(@Param("recetaId") Long recetaId);
    
    // Contar valoraciones por receta
    Long countByRecetaId(Long recetaId);
    
    // Encontrar las recetas mejor valoradas
    @Query("SELECT rv.receta.id, AVG(rv.puntuacion) as avgRating " +
           "FROM RecetaValoracion rv " +
           "GROUP BY rv.receta.id " +
           "HAVING COUNT(rv) >= :minRatings " +
           "ORDER BY avgRating DESC")
    List<Object[]> findTopRatedRecipes(@Param("minRatings") Long minRatings);
    
    // Distribución de valoraciones para una receta
    @Query("SELECT rv.puntuacion, COUNT(rv) " +
           "FROM RecetaValoracion rv " +
           "WHERE rv.receta.id = :recetaId " +
           "GROUP BY rv.puntuacion " +
           "ORDER BY rv.puntuacion")
    List<Object[]> getRatingDistribution(@Param("recetaId") Long recetaId);
} 