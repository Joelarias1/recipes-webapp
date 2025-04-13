package com.example.proyect.repository;

import com.example.proyect.model.RecetaComentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecetaComentarioRepository extends JpaRepository<RecetaComentario, Long> {
    
    // Buscar comentarios por receta (paginados)
    Page<RecetaComentario> findByRecetaIdAndAprobadoTrue(Long recetaId, Pageable pageable);
    
    // Buscar comentarios por usuario
    List<RecetaComentario> findByUsuarioId(Long usuarioId);
    
    // Buscar comentarios pendientes de moderación
    List<RecetaComentario> findByAprobadoFalse();
    
    // Buscar por contenido (búsqueda parcial)
    List<RecetaComentario> findByContenidoContainingIgnoreCase(String contenido);
    
    // Aprobar comentario
    @Modifying
    @Transactional
    @Query("UPDATE RecetaComentario c SET c.aprobado = true WHERE c.id = :comentarioId")
    void aprobarComentario(@Param("comentarioId") Long comentarioId);
    
    // Contar comentarios por receta
    Long countByRecetaId(Long recetaId);
    
    // Contar comentarios pendientes de moderación
    Long countByAprobadoFalse();
    
    // Buscar últimos comentarios aprobados (para mostrar en el dashboard)
    @Query("SELECT c FROM RecetaComentario c WHERE c.aprobado = true ORDER BY c.fechaCreacion DESC")
    List<RecetaComentario> findRecentApprovedComments(Pageable pageable);
} 