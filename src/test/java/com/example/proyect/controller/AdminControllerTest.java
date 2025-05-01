package com.example.proyect.controller;

import com.example.proyect.service.*; // Importar todos los servicios por si acaso
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mockear todos los servicios que AdminController pueda necesitar
    @MockBean private UsuarioService usuarioService;
    @MockBean private RecetaService recetaService;
    @MockBean private RecetaComentarioService comentarioService;
    @MockBean private RecetaValoracionService valoracionService;
    @MockBean private RecetaVideoService videoService;
    @MockBean private RecetaShareService shareService;
    @MockBean private CustomUserDetailsService customUserDetailsService; // Necesario por seguridad

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Simular usuario ADMIN autenticado
    void testPanelAdmin() throws Exception {
        when(usuarioService.contarUsuarios()).thenReturn(10L); 
        when(recetaService.contarRecetas()).thenReturn(25L); 
        when(comentarioService.contarComentariosPendientes()).thenReturn(5L); 

        mockMvc.perform(get("/admin/dashboard")) 
               .andExpect(status().isOk()) 
               .andExpect(view().name("admin/dashboard")) 
               .andExpect(model().attributeExists("totalUsuarios")) 
               .andExpect(model().attributeExists("totalRecetas"))
               .andExpect(model().attributeExists("comentariosPendientes"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"}) 
    void testPanelAdmin_AccesoDenegadoParaUsuarioNormal() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }
} 