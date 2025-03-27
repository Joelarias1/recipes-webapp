package com.example.proyect.controller;

import com.example.proyect.model.Receta;
import com.example.proyect.model.Usuario;
import com.example.proyect.service.RecetaService;
import com.example.proyect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar")
    public String listarRecetas(Model model) {
        model.addAttribute("recetas", recetaService.listarRecetasPublicas());
        return "recetas/listar";
    }
    
    @GetMapping("/buscar")
    public String buscarRecetas(@RequestParam(required = false) String nombre, Model model) {
        List<Receta> recetas;
        
        if (nombre != null && !nombre.isEmpty()) {
            recetas = recetaService.buscarRecetasPorNombre(nombre);
            model.addAttribute("terminoBusqueda", nombre);
        } else {
            recetas = recetaService.listarRecetasPublicas();
        }
        
        model.addAttribute("recetas", recetas);
        return "recetas/buscar";
    }
    
    @GetMapping("/detalle/{id}")
    public String detalleReceta(@PathVariable Long id, Model model) {
        Receta receta = recetaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        model.addAttribute("receta", receta);
        model.addAttribute("ingredientes", recetaService.listarIngredientesPorReceta(id));
        model.addAttribute("pasos", recetaService.listarPasosPorReceta(id));
        
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
        
        receta.setCreador(usuario);
        recetaService.guardarReceta(receta);
        
        return "redirect:/recetas/editar/" + receta.getId();
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
} 