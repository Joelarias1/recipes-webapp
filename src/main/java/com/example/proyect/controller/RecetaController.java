package com.example.proyect.controller;

import com.example.proyect.model.Receta;
import com.example.proyect.model.RecetaIngrediente;
import com.example.proyect.model.PasoReceta;
import com.example.proyect.model.RecetaComentario;
import com.example.proyect.model.RecetaVideo;
import com.example.proyect.model.Usuario;
import com.example.proyect.service.RecetaService;
import com.example.proyect.service.UsuarioService;
import com.example.proyect.service.RecetaValoracionService;
import com.example.proyect.service.RecetaComentarioService;
import com.example.proyect.service.RecetaVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RecetaValoracionService valoracionService;

    @Autowired
    private RecetaComentarioService comentarioService;

    @Autowired
    private RecetaVideoService videoService;

    @GetMapping("/listar")
    public String listarRecetas(Model model) {
        List<Receta> recetasRecientes = recetaService.listarRecetasRecientes();
        List<Receta> recetasPopulares = recetaService.listarRecetasPublicas();
        
        model.addAttribute("recetasRecientes", recetasRecientes);
        model.addAttribute("recetasPopulares", recetasPopulares);
        return "recetas/listar";
    }
    
    @GetMapping("/buscar")
    public String buscarRecetas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipoCocina,
            @RequestParam(required = false) String ingrediente,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) String dificultad,
            Model model) {
        
        List<Receta> recetas;
        boolean busquedaRealizada = nombre != null || tipoCocina != null || 
                                   ingrediente != null || pais != null || 
                                   dificultad != null;
        
        if (busquedaRealizada) {
            // Por ahora solo implementamos búsqueda por nombre y dificultad
            // Los demás filtros se implementarán cuando tengamos las tablas correspondientes
            if (nombre != null && !nombre.isEmpty()) {
                recetas = recetaService.buscarRecetasPorNombre(nombre);
            } else if (dificultad != null && !dificultad.isEmpty()) {
                recetas = recetaService.buscarRecetasPorDificultad(dificultad);
            } else {
                recetas = recetaService.listarRecetasPublicas();
            }
            model.addAttribute("busquedaRealizada", true);
        } else {
            recetas = recetaService.listarRecetasPublicas();
            model.addAttribute("busquedaRealizada", false);
        }
        
        // Agregar parámetros de búsqueda al modelo
        model.addAttribute("nombre", nombre);
        model.addAttribute("tipoCocina", tipoCocina);
        model.addAttribute("ingrediente", ingrediente);
        model.addAttribute("pais", pais);
        model.addAttribute("dificultad", dificultad);
        
        // Lista de dificultades para el selector
        model.addAttribute("dificultades", Arrays.asList("FACIL", "MEDIO", "DIFICIL"));
        
        model.addAttribute("recetas", recetas);
        return "recetas/buscar";
    }
    
    @GetMapping("/detalle/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model) {
        Optional<Receta> recetaOpt = recetaService.buscarPorId(id);
        
        if (recetaOpt.isEmpty()) {
            return "redirect:/recetas/listar";
        }
        
        Receta receta = recetaOpt.get();
        
        // Solo permitir ver recetas públicas (a menos que seas el creador o admin)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        if (!receta.isPublica() && !recetaService.esCreadorOAdmin(id, username)) {
            return "redirect:/recetas/listar";
        }
        
        List<RecetaIngrediente> ingredientes = recetaService.listarIngredientesPorReceta(id);
        List<PasoReceta> pasos = recetaService.listarPasosPorReceta(id);
        
        model.addAttribute("receta", receta);
        model.addAttribute("ingredientes", ingredientes);
        model.addAttribute("pasos", pasos);
        
        // Agregar información de valoraciones
        Double puntuacionPromedio = valoracionService.obtenerPuntuacionPromedio(id);
        Map<Integer, Long> distribucionValoraciones = valoracionService.obtenerDistribucionPuntuaciones(id);
        long totalValoraciones = distribucionValoraciones.values().stream().mapToLong(Long::longValue).sum();
        
        model.addAttribute("puntuacionPromedio", puntuacionPromedio);
        model.addAttribute("distribucionValoraciones", distribucionValoraciones);
        model.addAttribute("totalValoraciones", totalValoraciones);
        
        // Agregar comentarios recientes
        Page<RecetaComentario> comentarios = comentarioService.obtenerComentariosAprobados(
                id, PageRequest.of(0, 5)); // Primeros 5 comentarios
        model.addAttribute("comentarios", comentarios.getContent());
        model.addAttribute("totalComentarios", comentarios.getTotalElements());
        
        // Agregar videos
        List<RecetaVideo> videos = videoService.obtenerVideosPorReceta(id);
        model.addAttribute("videos", videos);
        
        // Verificar si el usuario actual ya ha valorado la receta
        if (!username.equals("anonymousUser")) {
            usuarioService.buscarPorUsername(username).ifPresent(usuario -> {
                valoracionService.buscarValoracionUsuario(id, usuario.getId())
                    .ifPresent(valoracion -> model.addAttribute("valoracionUsuario", valoracion));
            });
        }
        
        return "recetas/detalle";
    }
    
    @GetMapping("/mis-recetas")
    @PreAuthorize("isAuthenticated()")
    public String misRecetas(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.buscarPorUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("recetas", recetaService.listarRecetasPorUsuario(usuario.getId()));
        return "recetas/mis-recetas";
    }
    
    @GetMapping("/nueva")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'USER')")
    public String nuevaReceta(Model model) {
        model.addAttribute("receta", new Receta());
        return "recetas/form";
    }
    
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'USER')")
    public String guardarReceta(@ModelAttribute Receta receta) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.buscarPorUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (receta.getId() != null) {
            Receta recetaExistente = recetaService.buscarPorId(receta.getId())
                    .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
            
            if (!recetaExistente.getCreador().getId().equals(usuario.getId()) && 
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/acceso-denegado";
            }
            
            receta.setCreador(recetaExistente.getCreador());
            receta.setFechaCreacion(recetaExistente.getFechaCreacion());
        } else {
            receta.setCreador(usuario);
        }
        
        receta = recetaService.guardarReceta(receta);
        return "redirect:/recetas/detalle/" + receta.getId();
    }
    
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'USER')")
    public String editarReceta(@PathVariable Long id, Model model) {
        Receta receta = recetaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.buscarPorUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!receta.getCreador().getId().equals(usuario.getId()) && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/acceso-denegado";
        }
        
        model.addAttribute("receta", receta);
        model.addAttribute("ingredientes", recetaService.listarIngredientesPorReceta(id));
        model.addAttribute("ingredientesDisponibles", recetaService.listarIngredientesDisponibles());
        model.addAttribute("pasos", recetaService.listarPasosPorReceta(id));
        
        return "recetas/form";
    }
    
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'USER')")
    public String eliminarReceta(@PathVariable Long id) {
        Receta receta = recetaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.buscarPorUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!receta.getCreador().getId().equals(usuario.getId()) && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/acceso-denegado";
        }
        
        recetaService.eliminarReceta(id);
        return "redirect:/recetas/mis-recetas";
    }

    @PostMapping("/{id}/ingredientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'USER')")
    public String agregarIngrediente(@PathVariable Long id, 
                                   @RequestParam Long ingredienteId,
                                   @RequestParam String cantidad,
                                   @RequestParam String unidad) {
        recetaService.agregarIngredienteAReceta(id, ingredienteId, cantidad, unidad);
        return "redirect:/recetas/editar/" + id;
    }

    @PostMapping("/{recetaId}/ingredientes/{id}/eliminar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'USER')")
    public String eliminarIngrediente(@PathVariable Long recetaId, @PathVariable Long id) {
        recetaService.eliminarIngredienteDeReceta(recetaId, id);
        return "redirect:/recetas/editar/" + recetaId;
    }

    @PostMapping("/{id}/pasos")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'USER')")
    public String agregarPaso(@PathVariable Long id,
                            @RequestParam Integer numeroOrden,
                            @RequestParam String descripcion,
                            @RequestParam(required = false) String imagenUrl) {
        recetaService.agregarPasoAReceta(id, numeroOrden, descripcion, imagenUrl);
        return "redirect:/recetas/editar/" + id;
    }

    @PostMapping("/{recetaId}/pasos/{id}/eliminar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'USER')")
    public String eliminarPaso(@PathVariable Long recetaId, @PathVariable Long id) {
        recetaService.eliminarPasoDeReceta(recetaId, id);
        return "redirect:/recetas/editar/" + recetaId;
    }
} 