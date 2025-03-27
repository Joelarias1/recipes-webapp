package com.example.proyect.config;

import com.example.proyect.model.*;
import com.example.proyect.repository.*;
import com.example.proyect.service.RecetaService;
import com.example.proyect.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RecetaService recetaService;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private IngredienteRepository ingredienteRepository;
    
    @Autowired
    private RecetaIngredienteRepository recetaIngredienteRepository;
    
    @Autowired
    private PasoRecetaRepository pasoRecetaRepository;
    
    @PostConstruct
    @Transactional
    public void inicializarDatos() {
        // Crear usuarios iniciales (admin, chef, user)
        usuarioService.crearUsuariosIniciales();
        
        // Verificar si hay recetas existentes
        if (recetaRepository.count() == 0) {
            crearRecetasEjemplo();
        }
    }
    
    private void crearRecetasEjemplo() {
        System.out.println("Creando datos de ejemplo para la aplicación...");
        
        // Obtener usuario chef
        Usuario chef = usuarioService.buscarPorUsername("chef")
                .orElseThrow(() -> new RuntimeException("Usuario chef no encontrado"));
        
        // Crear ingredientes básicos
        Ingrediente huevo = crearIngrediente("Huevo", "Huevo de gallina");
        Ingrediente sal = crearIngrediente("Sal", "Sal de mesa");
        Ingrediente harina = crearIngrediente("Harina", "Harina de trigo");
        Ingrediente azucar = crearIngrediente("Azúcar", "Azúcar blanca refinada");
        Ingrediente aceite = crearIngrediente("Aceite", "Aceite de oliva");
        Ingrediente leche = crearIngrediente("Leche", "Leche entera");
        Ingrediente tomate = crearIngrediente("Tomate", "Tomate fresco");
        Ingrediente cebolla = crearIngrediente("Cebolla", "Cebolla blanca");
        Ingrediente papa = crearIngrediente("Papa", "Papa blanca");
        Ingrediente ajo = crearIngrediente("Ajo", "Diente de ajo");
        
        // 1. Tortilla Española
        Receta tortilla = new Receta();
        tortilla.setNombre("Tortilla Española");
        tortilla.setDescripcion("Una deliciosa tortilla española tradicional con patatas y cebolla.");
        tortilla.setTiempoPreparacion(45);
        tortilla.setDificultad("MEDIO");
        tortilla.setImagenUrl("https://www.recetasderechupete.com/wp-content/uploads/2020/11/Tortilla-de-patatas-4.jpg");
        tortilla.setPublica(true);
        tortilla.setCreador(chef);
        tortilla = recetaRepository.save(tortilla);
        
        // Ingredientes de la tortilla
        agregarIngrediente(tortilla, papa, "4", "unidades");
        agregarIngrediente(tortilla, cebolla, "1", "unidad");
        agregarIngrediente(tortilla, huevo, "6", "unidades");
        agregarIngrediente(tortilla, aceite, "100", "ml");
        agregarIngrediente(tortilla, sal, "5", "gr");
        
        // Pasos de la tortilla
        agregarPaso(tortilla, 1, "Pelar y cortar las papas en rodajas finas.", null);
        agregarPaso(tortilla, 2, "Pelar y cortar la cebolla en juliana.", null);
        agregarPaso(tortilla, 3, "Calentar el aceite en una sartén y freír las papas y la cebolla a fuego medio-bajo hasta que estén tiernas.", null);
        agregarPaso(tortilla, 4, "Escurrir el aceite y mezclar las papas y la cebolla con los huevos batidos y sazonados.", null);
        agregarPaso(tortilla, 5, "Verter la mezcla en la sartén y cocinar a fuego medio-bajo durante 5 minutos.", null);
        agregarPaso(tortilla, 6, "Darle la vuelta con ayuda de un plato y cocinar por el otro lado otros 5 minutos.", null);
        
        // 2. Bizcocho básico
        Receta bizcocho = new Receta();
        bizcocho.setNombre("Bizcocho Básico");
        bizcocho.setDescripcion("Un bizcocho esponjoso y delicioso, perfecto para cubrir con cremas o comer solo.");
        bizcocho.setTiempoPreparacion(60);
        bizcocho.setDificultad("FACIL");
        bizcocho.setImagenUrl("https://www.recetasderechupete.com/wp-content/uploads/2017/02/bizcocho_basico.jpg");
        bizcocho.setPublica(true);
        bizcocho.setCreador(chef);
        bizcocho = recetaRepository.save(bizcocho);
        
        // Ingredientes del bizcocho
        agregarIngrediente(bizcocho, huevo, "4", "unidades");
        agregarIngrediente(bizcocho, azucar, "250", "gr");
        agregarIngrediente(bizcocho, harina, "250", "gr");
        agregarIngrediente(bizcocho, leche, "100", "ml");
        agregarIngrediente(bizcocho, aceite, "100", "ml");
        
        // Pasos del bizcocho
        agregarPaso(bizcocho, 1, "Precalentar el horno a 180ºC.", null);
        agregarPaso(bizcocho, 2, "Batir los huevos con el azúcar hasta que la mezcla blanquee y aumente su volumen.", null);
        agregarPaso(bizcocho, 3, "Añadir el aceite y la leche, mezclar bien.", null);
        agregarPaso(bizcocho, 4, "Tamizar la harina e incorporarla a la mezcla anterior con movimientos envolventes.", null);
        agregarPaso(bizcocho, 5, "Verter la masa en un molde engrasado y enharinado.", null);
        agregarPaso(bizcocho, 6, "Hornear durante 40 minutos o hasta que al insertar un palillo, este salga limpio.", null);
        
        // 3. Salsa de tomate casera
        Receta salsa = new Receta();
        salsa.setNombre("Salsa de Tomate Casera");
        salsa.setDescripcion("Una salsa de tomate básica y versátil, ideal para pastas, pizzas y muchos otros platos.");
        salsa.setTiempoPreparacion(30);
        salsa.setDificultad("FACIL");
        salsa.setImagenUrl("https://www.recetasderechupete.com/wp-content/uploads/2018/06/Salsa-de-tomate-casera.jpg");
        salsa.setPublica(true);
        salsa.setCreador(chef);
        salsa = recetaRepository.save(salsa);
        
        // Ingredientes de la salsa
        agregarIngrediente(salsa, tomate, "1", "kg");
        agregarIngrediente(salsa, cebolla, "1", "unidad");
        agregarIngrediente(salsa, ajo, "2", "dientes");
        agregarIngrediente(salsa, aceite, "30", "ml");
        agregarIngrediente(salsa, sal, "5", "gr");
        
        // Pasos de la salsa
        agregarPaso(salsa, 1, "Escaldar los tomates en agua hirviendo durante 1 minuto, pelarlos y cortarlos en trozos.", null);
        agregarPaso(salsa, 2, "Picar finamente la cebolla y el ajo.", null);
        agregarPaso(salsa, 3, "Calentar el aceite en una cazuela y pochar la cebolla y el ajo.", null);
        agregarPaso(salsa, 4, "Añadir los tomates y cocinar a fuego medio durante 20 minutos, removiendo ocasionalmente.", null);
        agregarPaso(salsa, 5, "Salpimentar al gusto y triturar con una batidora hasta obtener una salsa suave.", null);
        
        System.out.println("Datos de ejemplo creados con éxito");
    }
    
    private Ingrediente crearIngrediente(String nombre, String descripcion) {
        return ingredienteRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> {
                    Ingrediente ingrediente = new Ingrediente();
                    ingrediente.setNombre(nombre);
                    ingrediente.setDescripcion(descripcion);
                    return ingredienteRepository.save(ingrediente);
                });
    }
    
    private void agregarIngrediente(Receta receta, Ingrediente ingrediente, String cantidad, String unidad) {
        RecetaIngrediente recetaIngrediente = new RecetaIngrediente();
        recetaIngrediente.setReceta(receta);
        recetaIngrediente.setIngrediente(ingrediente);
        recetaIngrediente.setCantidad(cantidad);
        recetaIngrediente.setUnidad(unidad);
        recetaIngredienteRepository.save(recetaIngrediente);
    }
    
    private void agregarPaso(Receta receta, Integer orden, String descripcion, String imagenUrl) {
        PasoReceta paso = new PasoReceta();
        paso.setReceta(receta);
        paso.setNumeroOrden(orden);
        paso.setDescripcion(descripcion);
        paso.setImagenUrl(imagenUrl);
        pasoRecetaRepository.save(paso);
    }
} 