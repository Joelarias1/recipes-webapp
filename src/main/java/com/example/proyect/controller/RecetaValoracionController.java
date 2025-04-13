package com.example.proyect.controller;

import com.example.proyect.model.RecetaValoracion;
import com.example.proyect.model.Usuario;
import com.example.proyect.service.RecetaService;
import com.example.proyect.service.RecetaValoracionService;
import com.example.proyect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/recetas")
public class RecetaValoracionController {

    @Autowired
    private RecetaValoracionService valoracionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RecetaService recetaService;

    /**
     * Valora una receta (o actualiza la valoración existente)
     */
    @PostMapping("/{recetaId}/valorar")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public Map<String, Object> valorarReceta(
            @PathVariable Long recetaId,
            @RequestParam Integer puntuacion,
            @RequestParam(required = false) String comentario,
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
            
            // Crear o actualizar la valoración
            RecetaValoracion valoracion = valoracionService.valorarReceta(
                    recetaId, 
                    usuario.getId(), 
                    puntuacion, 
                    comentario
            );
            
            // Obtener la nueva puntuación promedio
            Double promedio = valoracionService.obtenerPuntuacionPromedio(recetaId);
            Map<Integer, Long> distribucion = valoracionService.obtenerDistribucionPuntuaciones(recetaId);
            
            response.put("success", true);
            response.put("puntuacion", valoracion.getPuntuacion());
            response.put("promedio", promedio);
            response.put("distribucion", distribucion);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Elimina una valoración
     */
    @PostMapping("/valoraciones/{id}/eliminar")
    @PreAuthorize("isAuthenticated()")
    public String eliminarValoracion(
            @PathVariable Long id,
            @RequestParam Long recetaId,
            Authentication authentication) {
        
        try {
            // Verificar si el usuario es propietario de la valoración o es admin
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Comprobar si es admin
            boolean esAdmin = usuario.getRol().equals("ROLE_ADMIN");
            
            // Verificar propiedad de la valoración si no es admin
            if (!esAdmin) {
                valoracionService.buscarValoracionUsuario(recetaId, usuario.getId())
                    .orElseThrow(() -> new RuntimeException("No tienes permiso para eliminar esta valoración"));
            }
            
            valoracionService.eliminarValoracion(id);
            
        } catch (Exception e) {
            // Manejar error
        }
        
        return "redirect:/recetas/detalle/" + recetaId;
    }
} 