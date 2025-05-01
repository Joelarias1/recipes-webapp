package com.example.proyect.controller;

import com.example.proyect.model.RecetaValoracion;
import com.example.proyect.model.Usuario;
import com.example.proyect.service.RecetaValoracionService;
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

@WebMvcTest(RecetaValoracionController.class)
class RecetaValoracionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private RecetaValoracionService recetaValoracionService;
    @MockBean private UsuarioService usuarioService;
    @MockBean private CustomUserDetailsService customUserDetailsService; // Necesario por seguridad

    @Test
    @WithMockUser(username = "testuser") 
    void testAgregarValoracion() throws Exception {
        long recetaId = 1L;
        int puntuacion = 5;
        String comentario = "¡Excelente!";
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(5L);

        when(usuarioService.buscarPorUsername("testuser")).thenReturn(Optional.of(usuarioMock));

        when(recetaValoracionService.valorarReceta(eq(recetaId), eq(usuarioMock.getId()), eq(puntuacion), eq(comentario)))
                .thenReturn(new RecetaValoracion()); 

        mockMvc.perform(post("/recetas/{id}/valorar", recetaId)
                        .param("puntuacion", String.valueOf(puntuacion))
                        .param("comentario", comentario)
                        .with(csrf())) 
               .andExpect(status().is3xxRedirection()) 
               .andExpect(redirectedUrl("/recetas/" + recetaId)); 
    }

    // TODO: Implementar más pruebas si es necesario
} 