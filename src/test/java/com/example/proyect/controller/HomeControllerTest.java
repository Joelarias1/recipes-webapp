package com.example.proyect.controller;

import com.example.proyect.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService; 

    @Test
    void testHomePage() throws Exception {
   
        when(recetaService.listarRecetasRecientes()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/"))
               .andExpect(status().isOk()) 
               .andExpect(view().name("index")) 
               .andExpect(model().attributeExists("recetasRecientes")); 
    }
} 