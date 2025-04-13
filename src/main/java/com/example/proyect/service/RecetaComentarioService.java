package com.example.proyect.service;

import com.example.proyect.model.Receta;
import com.example.proyect.model.RecetaComentario;
import com.example.proyect.model.Usuario;
import com.example.proyect.repository.RecetaComentarioRepository;
import com.example.proyect.repository.RecetaRepository;
import com.example.proyect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecetaComentarioService {

    @Autowired
    private RecetaComentarioRepository comentarioRepository;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Crea un nuevo comentario para una receta
     */
    @Transactional
    public RecetaComentario crearComentario(Long recetaId, Long usuarioId, String contenido, boolean autoAprobado) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("La receta no existe"));
                
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
                
        RecetaComentario comentario = new RecetaComentario();
        comentario.setReceta(receta);
        comentario.setUsuario(usuario);
        comentario.setContenido(contenido);
        comentario.setAprobado(autoAprobado);
        
        return comentarioRepository.save(comentario);
    }
    
    /**
     * Obtiene comentarios de una receta (solo los aprobados)
     */
    public Page<RecetaComentario> obtenerComentariosAprobados(Long recetaId, Pageable pageable) {
        return comentarioRepository.findByRecetaIdAndAprobadoTrue(recetaId, pageable);
    }
    
    /**
     * Obtiene comentarios pendientes de moderación
     */
    public List<RecetaComentario> obtenerComentariosPendientes() {
        return comentarioRepository.findByAprobadoFalse();
    }
    
    /**
     * Aprueba un comentario
     */
    @Transactional
    public void aprobarComentario(Long comentarioId) {
        RecetaComentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new IllegalArgumentException("El comentario no existe"));
                
        comentario.setAprobado(true);
        comentarioRepository.save(comentario);
    }
    
    /**
     * Rechaza un comentario (lo elimina)
     */
    @Transactional
    public void rechazarComentario(Long comentarioId) {
        comentarioRepository.deleteById(comentarioId);
    }
    
    /**
     * Cuenta el número de comentarios pendientes
     */
    public Long contarComentariosPendientes() {
        return comentarioRepository.countByAprobadoFalse();
    }
    
    /**
     * Obtiene los comentarios recientes aprobados
     */
    public List<RecetaComentario> obtenerComentariosRecientes(int cantidad) {
        return comentarioRepository.findRecentApprovedComments(Pageable.ofSize(cantidad));
    }
} 