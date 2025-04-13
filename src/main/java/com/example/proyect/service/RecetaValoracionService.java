package com.example.proyect.service;

import com.example.proyect.model.Receta;
import com.example.proyect.model.RecetaValoracion;
import com.example.proyect.model.Usuario;
import com.example.proyect.repository.RecetaRepository;
import com.example.proyect.repository.RecetaValoracionRepository;
import com.example.proyect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RecetaValoracionService {

    @Autowired
    private RecetaValoracionRepository valoracionRepository;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Crea o actualiza una valoración de receta
     */
    @Transactional
    public RecetaValoracion valorarReceta(Long recetaId, Long usuarioId, Integer puntuacion, String comentario) {
        // Validar datos
        if (puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }
        
        // Buscar si ya existe una valoración del usuario para esta receta
        Optional<RecetaValoracion> valoracionExistente = valoracionRepository.findByRecetaIdAndUsuarioId(recetaId, usuarioId);
        
        if (valoracionExistente.isPresent()) {
            // Actualizar valoración existente
            RecetaValoracion valoracion = valoracionExistente.get();
            valoracion.setPuntuacion(puntuacion);
            valoracion.setComentario(comentario);
            return valoracionRepository.save(valoracion);
        } else {
            // Crear nueva valoración
            Receta receta = recetaRepository.findById(recetaId)
                    .orElseThrow(() -> new IllegalArgumentException("La receta no existe"));
                    
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
            
            RecetaValoracion nuevaValoracion = new RecetaValoracion();
            nuevaValoracion.setReceta(receta);
            nuevaValoracion.setUsuario(usuario);
            nuevaValoracion.setPuntuacion(puntuacion);
            nuevaValoracion.setComentario(comentario);
            
            return valoracionRepository.save(nuevaValoracion);
        }
    }
    
    /**
     * Obtiene la valoración promedio de una receta
     */
    public Double obtenerPuntuacionPromedio(Long recetaId) {
        Double promedio = valoracionRepository.calcularPuntuacionPromedio(recetaId);
        return promedio != null ? promedio : 0.0;
    }
    
    /**
     * Obtiene todas las valoraciones de una receta
     */
    public List<RecetaValoracion> obtenerValoracionesPorReceta(Long recetaId) {
        return valoracionRepository.findByRecetaId(recetaId);
    }
    
    /**
     * Obtiene la distribución de puntuaciones de una receta (cuántas de cada estrella)
     */
    public Map<Integer, Long> obtenerDistribucionPuntuaciones(Long recetaId) {
        List<Object[]> distribucion = valoracionRepository.getRatingDistribution(recetaId);
        Map<Integer, Long> resultado = new HashMap<>();
        
        // Inicializar todas las puntuaciones de 1 a 5 con valor 0
        for (int i = 1; i <= 5; i++) {
            resultado.put(i, 0L);
        }
        
        // Agregar los valores reales
        for (Object[] item : distribucion) {
            Integer puntuacion = (Integer) item[0];
            Long cantidad = (Long) item[1];
            resultado.put(puntuacion, cantidad);
        }
        
        return resultado;
    }
    
    /**
     * Elimina una valoración
     */
    @Transactional
    public void eliminarValoracion(Long valoracionId) {
        valoracionRepository.deleteById(valoracionId);
    }
    
    /**
     * Obtiene las recetas mejor valoradas
     */
    public List<Object[]> obtenerRecetasMejorValoradas(Long minValoraciones) {
        return valoracionRepository.findTopRatedRecipes(minValoraciones);
    }
    
    /**
     * Busca la valoración de un usuario para una receta
     */
    public Optional<RecetaValoracion> buscarValoracionUsuario(Long recetaId, Long usuarioId) {
        return valoracionRepository.findByRecetaIdAndUsuarioId(recetaId, usuarioId);
    }
} 