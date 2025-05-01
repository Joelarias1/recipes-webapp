package com.example.proyect.controller;

import com.example.proyect.model.RecetaVideo;
import com.example.proyect.service.RecetaVideoService;
import com.example.proyect.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecetaMediaController.class)
class RecetaMediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private RecetaVideoService recetaVideoService;
    @MockBean private CustomUserDetailsService customUserDetailsService; // Necesario por seguridad

    @Test
    @WithMockUser 
    void testObtenerVideosDeReceta() throws Exception {
        long recetaId = 1L;
        when(recetaVideoService.obtenerVideosPorReceta(recetaId)).thenReturn(new ArrayList<RecetaVideo>());

        mockMvc.perform(get("/recetas/{id}/videos", recetaId)) 
               .andExpect(status().isOk()) 
               .andExpect(view().name("receta-videos")) 
               .andExpect(model().attributeExists("recetaId")) 
               .andExpect(model().attributeExists("videos"));
    }

    @Test
    @WithMockUser 
    void testSubirVideoReceta() throws Exception {
        long recetaId = 1L;

        when(recetaVideoService.agregarVideo(eq(recetaId), anyString(), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(new RecetaVideo()); 

        mockMvc.perform(post("/recetas/{id}/videos/subir", recetaId) 
                        .param("titulo", "Título Video")
                        .param("descripcion", "Desc Video")
                        .with(csrf()))
               .andExpect(status().is3xxRedirection()) 
               .andExpect(redirectedUrl("/recetas/" + recetaId + "/videos")); 
    }

    // TODO: Implementar pruebas para otras rutas (ej. POST para subir video)
} 