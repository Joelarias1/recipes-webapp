package com.example.proyect.controller;

import com.example.proyect.model.RecetaComentario;
import com.example.proyect.model.Usuario;
import com.example.proyect.service.RecetaComentarioService;
import com.example.proyect.service.UsuarioService;
import com.example.proyect.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecetaComentarioController.class)
class RecetaComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private RecetaComentarioService comentarioService;
    @MockBean private UsuarioService usuarioService;
    @MockBean private CustomUserDetailsService customUserDetailsService; // Necesario por seguridad

    @Test
    @WithMockUser(username = "testuser") // Simular usuario autenticado
    void testAgregarComentario() throws Exception {
        long recetaId = 1L;
        String contenidoComentario = "Un comentario de prueba";
        Usuario usuarioMock = new Usuario(); 
        usuarioMock.setId(5L); 

        when(usuarioService.buscarPorUsername("testuser")).thenReturn(Optional.of(usuarioMock));

  
        when(comentarioService.crearComentario(eq(recetaId), eq(usuarioMock.getId()), eq(contenidoComentario), anyBoolean()))
                .thenReturn(new RecetaComentario()); 

        mockMvc.perform(post("/recetas/{id}/comentarios", recetaId)
                        .param("contenido", contenidoComentario)
                        .with(csrf())) 
               .andExpect(status().is3xxRedirection()) 
               .andExpect(redirectedUrl("/recetas/" + recetaId)); 
    }

     // TODO: Implementar más pruebas si es necesario (ej. aprobar, rechazar comentario si hay rutas)
} 