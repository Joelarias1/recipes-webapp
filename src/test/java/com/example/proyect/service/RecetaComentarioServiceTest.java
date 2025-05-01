package com.example.proyect.service;

import com.example.proyect.model.Receta;
import com.example.proyect.model.RecetaComentario;
import com.example.proyect.model.Usuario;
import com.example.proyect.repository.RecetaComentarioRepository;
import com.example.proyect.repository.RecetaRepository;
import com.example.proyect.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaComentarioServiceTest {

    @Mock
    private RecetaComentarioRepository comentarioRepository;

    @Mock
    private RecetaRepository recetaRepository; 

    @Mock
    private UsuarioRepository usuarioRepository; 

    @InjectMocks
    private RecetaComentarioService comentarioService;

    private RecetaComentario comentarioPendiente1;
    private RecetaComentario comentarioPendiente2;

    @BeforeEach
    void setUp() {
        Usuario usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        Receta recetaEjemplo = new Receta();
        recetaEjemplo.setId(1L);

        comentarioPendiente1 = new RecetaComentario();
        comentarioPendiente1.setId(20L);
        comentarioPendiente1.setContenido("Comentario Pendiente 1");
        comentarioPendiente1.setReceta(recetaEjemplo);
        comentarioPendiente1.setUsuario(usuarioEjemplo);
        comentarioPendiente1.setAprobado(false); 

        comentarioPendiente2 = new RecetaComentario();
        comentarioPendiente2.setId(21L);
        comentarioPendiente2.setContenido("Comentario Pendiente 2");
        comentarioPendiente2.setReceta(recetaEjemplo);
        comentarioPendiente2.setUsuario(usuarioEjemplo);
        comentarioPendiente2.setAprobado(false); 
    }

    @Test
    void testObtenerComentariosPendientes() {
        // Arrange
        when(comentarioRepository.findByAprobadoFalse()).thenReturn(Arrays.asList(comentarioPendiente1, comentarioPendiente2));

        // Act
        List<RecetaComentario> resultado = comentarioService.obtenerComentariosPendientes();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertFalse(resultado.get(0).isAprobado());
        assertFalse(resultado.get(1).isAprobado());
        verify(comentarioRepository, times(1)).findByAprobadoFalse();
        verifyNoMoreInteractions(comentarioRepository);
    }

    @Test
    void testAprobarComentario() {
        long comentarioIdParaAprobar = 20L;
        when(comentarioRepository.findById(comentarioIdParaAprobar)).thenReturn(Optional.of(comentarioPendiente1));
        when(comentarioRepository.save(any(RecetaComentario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        comentarioService.aprobarComentario(comentarioIdParaAprobar);

     
        verify(comentarioRepository, times(1)).findById(comentarioIdParaAprobar);

        ArgumentCaptor<RecetaComentario> comentarioCaptor = ArgumentCaptor.forClass(RecetaComentario.class);
        verify(comentarioRepository, times(1)).save(comentarioCaptor.capture());

        RecetaComentario comentarioGuardado = comentarioCaptor.getValue();
        assertTrue(comentarioGuardado.isAprobado(), "El comentario debería estar aprobado después de guardar");
    }
    
} 