package com.example.proyect.repository;

import com.example.proyect.model.RecetaShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecetaShareRepository extends JpaRepository<RecetaShare, Long> {
    
    // Buscar comparticiones por receta
    List<RecetaShare> findByRecetaId(Long recetaId);
    
    // Buscar comparticiones por usuario
    List<RecetaShare> findByUsuarioId(Long usuarioId);
    
    // Buscar comparticiones por plataforma
    List<RecetaShare> findByPlataforma(String plataforma);
    
    // Buscar por enlace generado
    RecetaShare findByEnlaceGenerado(String enlaceGenerado);
    
    // Encontrar las recetas más compartidas
    @Query("SELECT rs.receta.id, COUNT(rs) as shareCount FROM RecetaShare rs GROUP BY rs.receta.id ORDER BY shareCount DESC")
    List<Object[]> findMostSharedRecipes();
    
    // Incrementar contador de clicks para un enlace específico
    @Modifying
    @Transactional
    @Query("UPDATE RecetaShare rs SET rs.clicks = rs.clicks + 1 WHERE rs.id = :shareId")
    void incrementClickCount(@Param("shareId") Long shareId);
    
    // Contar comparticiones por receta
    Long countByRecetaId(Long recetaId);
    
    // Contar comparticiones por plataforma
    @Query("SELECT rs.plataforma, COUNT(rs) FROM RecetaShare rs GROUP BY rs.plataforma")
    List<Object[]> countSharesByPlatform();
} 