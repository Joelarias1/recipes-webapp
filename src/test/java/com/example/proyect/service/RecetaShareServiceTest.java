package com.example.proyect.service;

import com.example.proyect.model.Receta;
import com.example.proyect.model.RecetaShare;
import com.example.proyect.model.Usuario;
import com.example.proyect.repository.RecetaShareRepository;
import com.example.proyect.repository.RecetaRepository;
import com.example.proyect.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaShareServiceTest {

    @Mock
    private RecetaShareRepository recetaShareRepository;

    @Mock
    private RecetaRepository recetaRepository; 

    @Mock
    private UsuarioRepository usuarioRepository; 

    @InjectMocks
    private RecetaShareService recetaShareService;

    private Receta recetaEjemplo;
    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recetaShareService, "baseUrl", "http://test.com");

        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);

        recetaEjemplo = new Receta();
        recetaEjemplo.setId(1L);
        recetaEjemplo.setNombre("Receta Ejemplo"); 
    }

    @Test
    void testObtenerEstadisticasPorPlataforma() {
        Object[] statFacebook = {"Facebook", 5L};
        Object[] statTwitter = {"Twitter", 3L};
        List<Object[]> stats = Arrays.asList(statFacebook, statTwitter);

        when(recetaShareRepository.countSharesByPlatform()).thenReturn(stats);

        Map<String, Long> resultado = recetaShareService.obtenerEstadisticasPorPlataforma();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(5L, resultado.get("Facebook"));
        assertEquals(3L, resultado.get("Twitter"));
        verify(recetaShareRepository, times(1)).countSharesByPlatform();
        verifyNoMoreInteractions(recetaShareRepository);
    }

    @Test
    void testRegistrarClick() {
        long shareIdParaClick = 30L;
        doNothing().when(recetaShareRepository).incrementClickCount(shareIdParaClick);

        recetaShareService.registrarClick(shareIdParaClick);

        verify(recetaShareRepository, times(1)).incrementClickCount(shareIdParaClick);
    }

    @Test
    void testCompartirReceta_Exitoso() {
        long recetaId = 1L;
        long usuarioId = 1L;
        String plataforma = "LINK"; 

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));
        when(recetaShareRepository.save(any(RecetaShare.class))).thenAnswer(inv -> {
            RecetaShare share = inv.getArgument(0);
            share.setId(100L);
            return share;
        });

        RecetaShare resultado = recetaShareService.compartirReceta(recetaId, usuarioId, plataforma);

        assertNotNull(resultado);
        assertEquals(recetaId, resultado.getReceta().getId());
        assertEquals(usuarioId, resultado.getUsuario().getId());
        assertEquals(plataforma, resultado.getPlataforma());
        assertNotNull(resultado.getEnlaceGenerado());
        assertTrue(resultado.getEnlaceGenerado().startsWith("http://test.com/share/"));
        assertEquals(0, resultado.getClicks());

        verify(recetaRepository, times(1)).findById(recetaId);
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(recetaShareRepository, times(1)).save(any(RecetaShare.class));
    }

    @Test
    void testCompartirReceta_RecetaNoEncontrada() {
        long recetaIdInexistente = 99L;
        long usuarioId = 1L;
        String plataforma = "LINK";

        when(recetaRepository.findById(recetaIdInexistente)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            recetaShareService.compartirReceta(recetaIdInexistente, usuarioId, plataforma);
        });
        verify(recetaShareRepository, never()).save(any()); 
    }

    @Test
    void testCompartirReceta_UsuarioNoEncontrado() {
        long recetaId = 1L;
        long usuarioIdInexistente = 99L;
        String plataforma = "LINK";

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));
        when(usuarioRepository.findById(usuarioIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            recetaShareService.compartirReceta(recetaId, usuarioIdInexistente, plataforma);
        });
        verify(recetaShareRepository, never()).save(any());
    }

    @Test
    void testCompartirReceta_PlataformaInvalida() {
        // Arrange
        long recetaId = 1L;
        long usuarioId = 1L;
        String plataformaInvalida = "INVALIDA";

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            recetaShareService.compartirReceta(recetaId, usuarioId, plataformaInvalida);
        }, "Debería lanzar excepción por plataforma inválida");
        verify(recetaShareRepository, never()).save(any());
    }

    @Test
    void testCompartirReceta_Exitoso_Email() {
        long recetaId = 1L;
        long usuarioId = 1L;
        String plataforma = "EMAIL"; 

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));
        when(recetaShareRepository.save(any(RecetaShare.class))).thenAnswer(inv -> {
            RecetaShare share = inv.getArgument(0);
            share.setId(101L);
            return share;
        });

        RecetaShare resultado = recetaShareService.compartirReceta(recetaId, usuarioId, plataforma);

        // Assert
        assertNotNull(resultado);
        assertEquals(plataforma, resultado.getPlataforma());
        assertTrue(resultado.getEnlaceGenerado().startsWith("mailto:?"), "El enlace para email debería empezar con mailto:");
        verify(recetaShareRepository, times(1)).save(any(RecetaShare.class));
    }

    @Test
    void testObtenerRecetasMasCompartidas() {
        // Arrange
        Object[] receta1Stat = {1L, 10L}; // recetaId, count
        Object[] receta2Stat = {5L, 8L};
        List<Object[]> statsRepo = Arrays.asList(receta1Stat, receta2Stat);
        int limite = 5;

        when(recetaShareRepository.findMostSharedRecipes()).thenReturn(statsRepo);

        // Act
        List<Object[]> resultado = recetaShareService.obtenerRecetasMasCompartidas(limite);

        assertNotNull(resultado);
        assertEquals(2, resultado.size()); 
        assertEquals(1L, resultado.get(0)[0]);
        assertEquals(10L, resultado.get(0)[1]);
        assertEquals(5L, resultado.get(1)[0]);
        assertEquals(8L, resultado.get(1)[1]);
        verify(recetaShareRepository, times(1)).findMostSharedRecipes();
    }
} 