package com.example.proyect.controller;

import com.example.proyect.model.Receta;
import com.example.proyect.model.Usuario;
import com.example.proyect.service.RecetaService;
import com.example.proyect.service.UsuarioService;
import com.example.proyect.service.RecetaComentarioService;
import com.example.proyect.service.RecetaValoracionService;
import com.example.proyect.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecetaController.class)
class RecetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private RecetaService recetaService;
    @MockBean private UsuarioService usuarioService; // Asumiendo dependencia
    @MockBean private RecetaComentarioService comentarioService; // Asumiendo dependencia para mostrar comentarios
    @MockBean private RecetaValoracionService valoracionService; // Asumiendo dependencia para mostrar valoración
    @MockBean private CustomUserDetailsService customUserDetailsService; // Necesario por seguridad

    private Receta recetaEjemplo;
    private Usuario usuarioEjemplo;

    @BeforeEach
    void setup() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setUsername("testuser");

        recetaEjemplo = new Receta();
        recetaEjemplo.setId(1L);
        recetaEjemplo.setNombre("Receta de Prueba");
        recetaEjemplo.setDescripcion("Descripción de prueba");
        recetaEjemplo.setCreador(usuarioEjemplo);
        recetaEjemplo.setPublica(true);
    }

    @Test
    void testVerDetalleReceta() throws Exception {
        // Arrange
        long recetaId = 1L;
        when(recetaService.buscarPorId(recetaId)).thenReturn(Optional.of(recetaEjemplo));
        // Simular que no hay comentarios o valoraciones para simplificar
        when(comentarioService.obtenerComentariosAprobados(eq(recetaId), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(valoracionService.obtenerPuntuacionPromedio(recetaId)).thenReturn(0.0);
        when(valoracionService.buscarValoracionUsuario(eq(recetaId), anyLong())).thenReturn(Optional.empty()); 

        // Act & Assert
        mockMvc.perform(get("/recetas/{id}", recetaId))
               .andExpect(status().isOk())
               .andExpect(view().name("receta-detalle")) 
               .andExpect(model().attributeExists("receta"))
               .andExpect(model().attribute("receta", recetaEjemplo))
               .andExpect(model().attributeExists("comentarios"))
               .andExpect(model().attributeExists("promedio"));
    }

    @Test
    void testVerDetalleReceta_NoEncontrada() throws Exception {
        // Arrange
        long recetaId = 99L;
        when(recetaService.buscarPorId(recetaId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/recetas/{id}", recetaId))
               .andExpect(status().isNotFound()); 
    }

    @Test
    @WithMockUser // Simula un usuario autenticado
    void testMostrarFormularioNuevaReceta() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/recetas/nueva")) 
               .andExpect(status().isOk()) 
               .andExpect(view().name("formulario-receta")) 
               .andExpect(model().attributeExists("receta")); 
    }

} 