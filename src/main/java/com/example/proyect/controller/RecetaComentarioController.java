package com.example.proyect.controller;

import com.example.proyect.model.RecetaComentario;
import com.example.proyect.model.Usuario;
import com.example.proyect.service.RecetaComentarioService;
import com.example.proyect.service.RecetaService;
import com.example.proyect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/recetas")
public class RecetaComentarioController {

    @Autowired
    private RecetaComentarioService comentarioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RecetaService recetaService;
    
    /**
     * Añadir un comentario a una receta
     */
    @PostMapping("/{recetaId}/comentar")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public Map<String, Object> comentarReceta(
            @PathVariable Long recetaId,
            @RequestParam String contenido,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Verificar si la receta existe
            if (!recetaService.buscarPorId(recetaId).isPresent()) {
                response.put("success", false);
                response.put("error", "Receta no encontrada");
                return response;
            }
            
            // Determinar si el comentario debe ser aprobado automáticamente
            // (admin o creador de la receta)
            boolean autoAprobado = usuario.getRol().equals("ROLE_ADMIN") || 
                                  recetaService.esCreadorOAdmin(recetaId, username);
            
            // Crear el comentario
            RecetaComentario comentario = comentarioService.crearComentario(
                    recetaId, 
                    usuario.getId(), 
                    contenido, 
                    autoAprobado
            );
            
            response.put("success", true);
            response.put("comentarioId", comentario.getId());
            response.put("aprobado", comentario.isAprobado());
            response.put("mensaje", comentario.isAprobado() 
                ? "Tu comentario ha sido publicado" 
                : "Tu comentario está pendiente de aprobación");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Cargar más comentarios para una receta (paginación)
     */
    @GetMapping("/{recetaId}/comentarios")
    @ResponseBody
    public Map<String, Object> cargarComentarios(
            @PathVariable Long recetaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<RecetaComentario> comentarios = comentarioService.obtenerComentariosAprobados(
                    recetaId, 
                    PageRequest.of(pagina, tamanio)
            );
            
            response.put("success", true);
            response.put("comentarios", comentarios.getContent());
            response.put("totalPaginas", comentarios.getTotalPages());
            response.put("totalElementos", comentarios.getTotalElements());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Panel de moderación de comentarios (solo admins)
     */
    @GetMapping("/admin/comentarios")
    @PreAuthorize("hasRole('ADMIN')")
    public String panelModeracion(Model model) {
        model.addAttribute("comentariosPendientes", comentarioService.obtenerComentariosPendientes());
        return "admin/moderacion-comentarios";
    }
    
    /**
     * Aprobar un comentario
     */
    @PostMapping("/admin/comentarios/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> aprobarComentario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            comentarioService.aprobarComentario(id);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Rechazar un comentario
     */
    @PostMapping("/admin/comentarios/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> rechazarComentario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            comentarioService.rechazarComentario(id);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
} 