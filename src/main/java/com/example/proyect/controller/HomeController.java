package com.example.proyect.controller;

import com.example.proyect.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private RecetaService recetaService;

    @GetMapping({"/", "/home", "/index"})
    public String home(Model model) {
        model.addAttribute("recetasRecientes", recetaService.listarRecetasRecientes());
        model.addAttribute("titulo", "Inicio - RecetaApp");
        return "index";
    }
    
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("titulo", "Iniciar Sesión - RecetaApp");
        return "login";
    }
    
    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Model model) {
        model.addAttribute("titulo", "Acceso Denegado - RecetaApp");
        return "error/acceso-denegado";
    }
} 