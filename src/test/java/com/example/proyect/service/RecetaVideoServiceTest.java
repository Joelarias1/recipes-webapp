package com.example.proyect.service;

import com.example.proyect.model.Receta;
import com.example.proyect.model.RecetaVideo;
import com.example.proyect.repository.RecetaRepository;
import com.example.proyect.repository.RecetaVideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaVideoServiceTest {

    @Mock
    private RecetaVideoRepository recetaVideoRepository;

    @Mock
    private RecetaRepository recetaRepository; 

    @InjectMocks
    private RecetaVideoService recetaVideoService;

    private RecetaVideo videoEjemplo1;
    private RecetaVideo videoEjemplo2;
    private Receta recetaEjemplo;

    @BeforeEach
    void setUp() {
        recetaEjemplo = new Receta();
        recetaEjemplo.setId(1L);

        videoEjemplo1 = new RecetaVideo();
        videoEjemplo1.setId(5L);
        videoEjemplo1.setTitulo("Video Test 1");
        videoEjemplo1.setVideoUrl("http://example.com/video1.mp4");
        videoEjemplo1.setReceta(recetaEjemplo);

        videoEjemplo2 = new RecetaVideo();
        videoEjemplo2.setId(6L);
        videoEjemplo2.setTitulo("Video Test 2");
        videoEjemplo2.setVideoUrl("http://example.com/video2.mp4");
        videoEjemplo2.setReceta(recetaEjemplo);
    }

    @Test
    void testObtenerVideosPorReceta() {
        // Arrange
        long recetaId = 1L;
        when(recetaVideoRepository.findByRecetaId(recetaId)).thenReturn(Arrays.asList(videoEjemplo1, videoEjemplo2));

        // Act
        List<RecetaVideo> resultado = recetaVideoService.obtenerVideosPorReceta(recetaId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(videoEjemplo1.getTitulo(), resultado.get(0).getTitulo());
        assertEquals(videoEjemplo2.getTitulo(), resultado.get(1).getTitulo());
        verify(recetaVideoRepository, times(1)).findByRecetaId(recetaId);
        verifyNoMoreInteractions(recetaVideoRepository);
    }

    @Test
    void testEliminarVideo() {
        long videoIdParaEliminar = 5L;
         doNothing().when(recetaVideoRepository).deleteById(videoIdParaEliminar);

        recetaVideoService.eliminarVideo(videoIdParaEliminar);

        verify(recetaVideoRepository, times(1)).deleteById(videoIdParaEliminar);
    }

    @Test
    void testAgregarVideo_Exitoso() {
        long recetaId = 1L;
        String titulo = "Nuevo Video";
        String urlOriginal = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"; 
        String descripcion = "Descripción";
        Integer duracion = 212;
        String formatoEsperado = "youtube";
        String urlNormalizadaEsperada = "https://www.youtube.com/embed/dQw4w9WgXcQ";

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));
        // Simular guardado
        when(recetaVideoRepository.save(any(RecetaVideo.class))).thenAnswer(inv -> {
            RecetaVideo video = inv.getArgument(0);
            video.setId(100L); // Asignar ID simulado
            return video;
        });

        RecetaVideo resultado = recetaVideoService.agregarVideo(recetaId, titulo, urlOriginal, descripcion, duracion, null); 

        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(titulo, resultado.getTitulo());
        assertEquals(urlNormalizadaEsperada, resultado.getVideoUrl()); 
        assertEquals(formatoEsperado, resultado.getFormato()); 
        assertEquals(recetaId, resultado.getReceta().getId());

        verify(recetaRepository, times(1)).findById(recetaId);
        ArgumentCaptor<RecetaVideo> captor = ArgumentCaptor.forClass(RecetaVideo.class);
        verify(recetaVideoRepository, times(1)).save(captor.capture());
        assertEquals(urlNormalizadaEsperada, captor.getValue().getVideoUrl());
    }

    @Test
    void testAgregarVideo_RecetaNoEncontrada() {
        long recetaIdInexistente = 99L;
        when(recetaRepository.findById(recetaIdInexistente)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            recetaVideoService.agregarVideo(recetaIdInexistente, "Titulo", "url", "desc", 120, "formato");
        });
        verify(recetaVideoRepository, never()).save(any());
    }

    @Test
    void testBuscarVideosPorTitulo() {
        // Arrange
        String termino = "Test";
        List<RecetaVideo> listaEncontrada = Arrays.asList(videoEjemplo1, videoEjemplo2);
        when(recetaVideoRepository.findByTituloContainingIgnoreCase(termino)).thenReturn(listaEncontrada);

        List<RecetaVideo> resultado = recetaVideoService.buscarVideosPorTitulo(termino);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(recetaVideoRepository, times(1)).findByTituloContainingIgnoreCase(termino);
    }

} 