package com.example.proyect.service;

import com.example.proyect.model.*;
import com.example.proyect.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private RecetaIngredienteRepository recetaIngredienteRepository;
    
    @Autowired
    private PasoRecetaRepository pasoRecetaRepository;
    
    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Receta> listarRecetasPublicas() {
        return recetaRepository.findByPublicaTrue();
    }
    
    public List<Receta> listarRecetasRecientes() {
        return recetaRepository.findRecentPublicRecipes();
    }
    
    public List<Receta> buscarRecetasPorNombre(String nombre) {
        return recetaRepository.findByNombreContainingIgnoreCaseAndPublicaTrue(nombre);
    }
    
    public List<Receta> buscarRecetasPorDificultad(String dificultad) {
        return recetaRepository.findByDificultadAndPublicaTrue(dificultad);
    }
    
    public List<Receta> listarRecetasPorUsuario(Long usuarioId) {
        return recetaRepository.findByCreadorId(usuarioId);
    }

    /**
     * Lista todas las recetas sin importar su estado
     */
    public List<Receta> listarTodasLasRecetas() {
        return recetaRepository.findAll();
    }
    
    public List<Ingrediente> listarIngredientesDisponibles() {
        return ingredienteRepository.findAll();
    }
    
    /**
     * Cuenta el número total de recetas
     */
    public long contarRecetas() {
        return recetaRepository.count();
    }
    
    public Optional<Receta> buscarPorId(Long id) {
        return recetaRepository.findById(id);
    }
    
    @Transactional
    public Receta guardarReceta(Receta receta) {
        return recetaRepository.save(receta);
    }
    
    @Transactional
    public void eliminarReceta(Long id) {
        // Eliminar pasos e ingredientes relacionados primero
        pasoRecetaRepository.deleteByRecetaId(id);
        recetaIngredienteRepository.deleteByRecetaId(id);
        
        // Eliminar la receta
        recetaRepository.deleteById(id);
    }
    
    public List<RecetaIngrediente> listarIngredientesPorReceta(Long recetaId) {
        return recetaIngredienteRepository.findByRecetaId(recetaId);
    }
    
    public List<PasoReceta> listarPasosPorReceta(Long recetaId) {
        return pasoRecetaRepository.findByRecetaIdOrderByNumeroOrden(recetaId);
    }
    
    @Transactional
    public RecetaIngrediente agregarIngredienteAReceta(Long recetaId, Long ingredienteId, String cantidad, String unidad) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));
        
        RecetaIngrediente recetaIngrediente = new RecetaIngrediente();
        recetaIngrediente.setReceta(receta);
        recetaIngrediente.setIngrediente(ingrediente);
        recetaIngrediente.setCantidad(cantidad);
        recetaIngrediente.setUnidad(unidad);
        
        return recetaIngredienteRepository.save(recetaIngrediente);
    }
    
    @Transactional
    public PasoReceta agregarPasoAReceta(Long recetaId, Integer numeroOrden, String descripcion, String imagenUrl) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        PasoReceta paso = new PasoReceta();
        paso.setReceta(receta);
        paso.setNumeroOrden(numeroOrden);
        paso.setDescripcion(descripcion);
        paso.setImagenUrl(imagenUrl);
        
        return pasoRecetaRepository.save(paso);
    }
    
    @Transactional
    public void eliminarIngredienteDeReceta(Long recetaId, Long ingredienteId) {
        recetaIngredienteRepository.deleteByRecetaIdAndId(recetaId, ingredienteId);
    }
    
    @Transactional
    public void eliminarPasoDeReceta(Long recetaId, Long pasoId) {
        pasoRecetaRepository.deleteByRecetaIdAndId(recetaId, pasoId);
    }

    /**
     * Verifica si el usuario actual es el creador de la receta o un administrador
     */
    public boolean esCreadorOAdmin(Long recetaId, String username) {
        // Obtener la receta
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        
        // Comprobar si es el creador
        boolean esCreador = receta.getCreador().getUsername().equals(username);
        
        // Comprobar si es admin (asumiendo que los admin tienen rol "ROLE_ADMIN")
        boolean esAdmin = usuarioRepository.findByUsername(username)
                .map(u -> u.getRol().equals("ROLE_ADMIN"))
                .orElse(false);
        
        return esCreador || esAdmin;
    }
} 