package com.example.proyect.controller;

import com.example.proyect.model.RecetaShare;
import com.example.proyect.model.RecetaVideo;
import com.example.proyect.model.Usuario;
import com.example.proyect.service.RecetaService;
import com.example.proyect.service.RecetaShareService;
import com.example.proyect.service.RecetaVideoService;
import com.example.proyect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recetas")
public class RecetaMediaController {

    @Autowired
    private RecetaShareService shareService;
    
    @Autowired
    private RecetaVideoService videoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RecetaService recetaService;
    
    /**
     * Compartir una receta en una plataforma
     */
    @PostMapping("/{recetaId}/compartir")
    @ResponseBody
    public Map<String, String> compartirReceta(@PathVariable Long recetaId,
                                            @RequestParam String plataforma,
                                            Authentication authentication) {
        
        Map<String, String> result = new HashMap<>();
        
        if (authentication != null) {
            try {
                String username = authentication.getName();
                Usuario usuario = usuarioService.buscarPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
                RecetaShare share = shareService.compartirReceta(recetaId, usuario.getId(), plataforma);
                result.put("status", "success");
                result.put("url", share.getEnlaceGenerado());
            } catch (Exception e) {
                result.put("status", "error");
                result.put("message", e.getMessage());
            }
        } else {
            result.put("status", "error");
            result.put("message", "Debes iniciar sesión para compartir una receta");
        }
        
        return result;
    }
    
    /**
     * Registrar un click en un enlace compartido
     */
    @GetMapping("/share/{token}")
    public String seguirEnlaceCompartido(@PathVariable String token) {
        // Aquí implementaríamos la lógica para encontrar el enlace por token
        // y redirigir a la receta correspondiente
        // También registraríamos el click
        
        return "redirect:/recetas/listar";
    }
    
    /**
     * Obtener videos de una receta
     */
    @GetMapping("/{recetaId}/videos")
    @ResponseBody
    public List<RecetaVideo> obtenerVideos(@PathVariable Long recetaId) {
        return videoService.obtenerVideosPorReceta(recetaId);
    }
    
    /**
     * Añadir un video a una receta (solo para el creador o admin)
     */
    @PostMapping("/{recetaId}/videos")
    public String agregarVideo(@PathVariable Long recetaId,
                             @RequestParam String titulo,
                             @RequestParam String videoUrl,
                             @RequestParam(required = false) String descripcion,
                             Authentication authentication) {
        
        if (authentication != null && recetaService.esCreadorOAdmin(recetaId, authentication.getName())) {
            videoService.agregarVideo(recetaId, titulo, videoUrl, descripcion, null, null);
        }
        
        return "redirect:/recetas/detalle/" + recetaId + "#videos";
    }
    
    /**
     * Eliminar un video
     */
    @PostMapping("/videos/{videoId}/eliminar")
    public String eliminarVideo(@PathVariable Long videoId,
                              @RequestParam Long recetaId,
                              Authentication authentication) {
        
        if (authentication != null && recetaService.esCreadorOAdmin(recetaId, authentication.getName())) {
            videoService.eliminarVideo(videoId);
        }
        
        return "redirect:/recetas/detalle/" + recetaId + "#videos";
    }
} 