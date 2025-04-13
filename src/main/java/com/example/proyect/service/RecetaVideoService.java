package com.example.proyect.service;

import com.example.proyect.model.Receta;
import com.example.proyect.model.RecetaVideo;
import com.example.proyect.repository.RecetaRepository;
import com.example.proyect.repository.RecetaVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RecetaVideoService {

    @Autowired
    private RecetaVideoRepository videoRepository;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    // Patrones regex para extraer información de videos
    private static final Pattern YOUTUBE_PATTERN = 
            Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/watch\\?v=|youtu\\.be\\/)([^&\\s]+).*$");
    
    private static final Pattern VIMEO_PATTERN = 
            Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?(?:vimeo\\.com\\/)(\\d+).*$");
    
    /**
     * Agrega un video a una receta
     */
    @Transactional
    public RecetaVideo agregarVideo(Long recetaId, String titulo, String videoUrl, String descripcion, 
                                    Integer duracionSegundos, String formato) {
        
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("La receta no existe"));
        
        // Normalizar la URL del video si es de una plataforma conocida
        Map<String, String> infoVideo = normalizarUrl(videoUrl);
        String urlNormalizada = infoVideo.get("url");
        
        // Si no se proporcionó un formato, intentar obtenerlo de la URL
        if (formato == null || formato.isEmpty()) {
            formato = infoVideo.get("formato");
        }
        
        RecetaVideo video = new RecetaVideo();
        video.setReceta(receta);
        video.setTitulo(titulo);
        video.setVideoUrl(urlNormalizada);
        video.setDescripcion(descripcion);
        video.setDuracionSegundos(duracionSegundos);
        video.setFormato(formato);
        
        return videoRepository.save(video);
    }
    
    /**
     * Obtiene todos los videos de una receta
     */
    public List<RecetaVideo> obtenerVideosPorReceta(Long recetaId) {
        return videoRepository.findByRecetaId(recetaId);
    }
    
    /**
     * Elimina un video
     */
    @Transactional
    public void eliminarVideo(Long videoId) {
        videoRepository.deleteById(videoId);
    }
    
    /**
     * Busca videos por título
     */
    public List<RecetaVideo> buscarVideosPorTitulo(String titulo) {
        return videoRepository.findByTituloContainingIgnoreCase(titulo);
    }
    
    /**
     * Normaliza la URL del video según la plataforma
     */
    private Map<String, String> normalizarUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            throw new IllegalArgumentException("La URL del video no puede estar vacía");
        }
        
        // Verificar si es una URL de YouTube
        Matcher youtubeMatcher = YOUTUBE_PATTERN.matcher(videoUrl);
        if (youtubeMatcher.find()) {
            String videoId = youtubeMatcher.group(1);
            return Map.of(
                "url", "https://www.youtube.com/embed/" + videoId,
                "formato", "youtube"
            );
        }
        
        // Verificar si es una URL de Vimeo
        Matcher vimeoMatcher = VIMEO_PATTERN.matcher(videoUrl);
        if (vimeoMatcher.find()) {
            String videoId = vimeoMatcher.group(1);
            return Map.of(
                "url", "https://player.vimeo.com/video/" + videoId,
                "formato", "vimeo"
            );
        }
        
        // Si no coincide con ningún patrón conocido, devolver la URL original
        return Map.of(
            "url", videoUrl,
            "formato", "otro"
        );
    }
} 