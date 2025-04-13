package com.example.proyect.service;

import com.example.proyect.model.Receta;
import com.example.proyect.model.RecetaShare;
import com.example.proyect.model.Usuario;
import com.example.proyect.repository.RecetaRepository;
import com.example.proyect.repository.RecetaShareRepository;
import com.example.proyect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecetaShareService {

    @Autowired
    private RecetaShareRepository shareRepository;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    /**
     * Genera un enlace compartible para una receta en una plataforma específica
     */
    @Transactional
    public RecetaShare compartirReceta(Long recetaId, Long usuarioId, String plataforma) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("La receta no existe"));
                
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
                
        // Validar que la plataforma sea válida
        if (!esPlataformaValida(plataforma)) {
            throw new IllegalArgumentException("Plataforma no soportada: " + plataforma);
        }
        
        // Generar un enlace único para compartir
        String enlaceUnico = generarEnlaceUnico(recetaId, plataforma);
        
        RecetaShare share = new RecetaShare();
        share.setReceta(receta);
        share.setUsuario(usuario);
        share.setPlataforma(plataforma);
        share.setEnlaceGenerado(enlaceUnico);
        share.setClicks(0);
        
        return shareRepository.save(share);
    }
    
    /**
     * Registra un click en un enlace compartido
     */
    @Transactional
    public void registrarClick(Long shareId) {
        shareRepository.incrementClickCount(shareId);
    }
    
    /**
     * Obtiene las estadísticas de compartición por plataforma
     */
    public Map<String, Long> obtenerEstadisticasPorPlataforma() {
        List<Object[]> stats = shareRepository.countSharesByPlatform();
        return stats.stream()
                .collect(Collectors.toMap(
                    obj -> (String) obj[0],
                    obj -> (Long) obj[1]
                ));
    }
    
    /**
     * Obtiene las recetas más compartidas
     */
    public List<Object[]> obtenerRecetasMasCompartidas(int limite) {
        return shareRepository.findMostSharedRecipes().stream()
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    /**
     * Verifica si una plataforma es válida
     */
    private boolean esPlataformaValida(String plataforma) {
        return plataforma != null && (
            plataforma.equals("FACEBOOK") ||
            plataforma.equals("TWITTER") ||
            plataforma.equals("INSTAGRAM") ||
            plataforma.equals("EMAIL") ||
            plataforma.equals("LINK")
        );
    }
    
    /**
     * Genera un enlace único para compartir
     */
    private String generarEnlaceUnico(Long recetaId, String plataforma) {
        String token = UUID.randomUUID().toString().substring(0, 8);
        String enlace = baseUrl + "/share/" + token;
        
        if (plataforma.equals("FACEBOOK")) {
            return "https://www.facebook.com/sharer/sharer.php?u=" + enlace;
        } else if (plataforma.equals("TWITTER")) {
            return "https://twitter.com/intent/tweet?url=" + enlace;
        } else if (plataforma.equals("INSTAGRAM")) {
            // Instagram no tiene API directa para compartir, usamos el enlace base
            return enlace;
        } else if (plataforma.equals("EMAIL")) {
            Receta receta = recetaRepository.findById(recetaId).orElse(null);
            String asunto = receta != null ? "Receta: " + receta.getNombre() : "Receta compartida";
            return "mailto:?subject=" + asunto + "&body=Te comparto esta receta: " + enlace;
        } else {
            // LINK o cualquier otro caso
            return enlace;
        }
    }
} 