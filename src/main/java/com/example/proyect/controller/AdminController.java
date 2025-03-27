package com.example.proyect.controller;

import com.example.proyect.model.Usuario;
import com.example.proyect.service.UsuarioService;
import com.example.proyect.service.RecetaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RecetaService recetaService;
    
    /**
     * Muestra el dashboard principal con estadísticas
     */
    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        // Obtener datos para el dashboard
        long totalUsuarios = usuarioService.contarUsuarios();
        long totalRecetas = recetaService.contarRecetas();
        List<Usuario> ultimosUsuarios = usuarioService.listarUltimosUsuarios(5);
        
        // Agregar datos al modelo
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalRecetas", totalRecetas);
        model.addAttribute("ultimosUsuarios", ultimosUsuarios);
        
        return "admin/dashboard";
    }
    
    /**
     * Lista todas las recetas
     */
    @GetMapping("/recetas")
    public String listarRecetas(Model model) {
        model.addAttribute("recetas", recetaService.listarTodasLasRecetas());
        return "admin/recetas";
    }
    
    /**
     * Lista todos los usuarios con paginación y filtros
     */
    @GetMapping("/usuarios")
    public String listarUsuarios(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            Model model) {
        
        // Obtener usuarios con filtros y paginación
        List<Usuario> usuarios = usuarioService.listarUsuarios(buscar, rol, activo, pagina, tamano);
        long totalUsuarios = usuarioService.contarUsuariosFiltrados(buscar, rol, activo);
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("buscar", buscar);
        model.addAttribute("rol", rol);
        model.addAttribute("activo", activo);
        model.addAttribute("pagina", pagina);
        model.addAttribute("tamano", tamano);
        
        return "admin/usuarios";
    }
    
    /**
     * Muestra el formulario para crear un nuevo usuario
     */
    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/usuario-form";
    }
    
    /**
     * Muestra el formulario para editar un usuario existente
     */
    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            return "admin/usuario-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }
    }
    
    /**
     * Muestra los detalles de un usuario
     */
    @GetMapping("/usuarios/ver/{id}")
    public String verUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            // Obtener recetas del usuario si es necesario
            model.addAttribute("recetas", recetaService.listarRecetasPorUsuario(id));
            return "admin/usuario-detalle";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }
    }
    
    /**
     * Guarda un usuario (nuevo o existente)
     */
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.guardarUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario guardado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }
    
    /**
     * Activa un usuario
     */
    @PostMapping("/usuarios/activar/{id}")
    public String activarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.activarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario activado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }
    
    /**
     * Desactiva un usuario
     */
    @PostMapping("/usuarios/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.desactivarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario desactivado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }
    
    /**
     * Elimina un usuario
     */
    @PostMapping("/usuarios/eliminar")
    public String eliminarUsuario(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }

    /**
     * Resetea la contraseña de un usuario
     */
    @GetMapping("/usuarios/reset-password/{id}")
    public String mostrarFormResetPassword(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            return "admin/reset-password";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }
    }

    /**
     * Actualiza la contraseña del usuario
     */
    @PostMapping("/usuarios/reset-password")
    public String resetearPassword(@RequestParam Long id, @RequestParam String password, @RequestParam String confirmarPassword, 
                                 RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmarPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/admin/usuarios/reset-password/" + id;
        }
        
        try {
            usuarioService.resetearPassword(id, password);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña actualizada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la contraseña: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }
} 