package com.example.proyect.service;

import com.example.proyect.model.Receta;
import com.example.proyect.model.RecetaValoracion;
import com.example.proyect.model.Usuario;
import com.example.proyect.repository.RecetaValoracionRepository;
import com.example.proyect.repository.RecetaRepository;
import com.example.proyect.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaValoracionServiceTest {

    @Mock
    private RecetaValoracionRepository recetaValoracionRepository;

    @Mock
    private RecetaRepository recetaRepository; 

    @Mock
    private UsuarioRepository usuarioRepository; 

    @InjectMocks
    private RecetaValoracionService recetaValoracionService;

    private RecetaValoracion valoracionEjemplo;
    private Long recetaIdEjemplo = 1L;
    private Long usuarioIdEjemplo = 1L;

    @BeforeEach
    void setUp() {
        Usuario usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(this.usuarioIdEjemplo);
        Receta recetaEjemplo = new Receta();
        recetaEjemplo.setId(this.recetaIdEjemplo);

        valoracionEjemplo = new RecetaValoracion();
        valoracionEjemplo.setId(40L);
        valoracionEjemplo.setPuntuacion(3);
        valoracionEjemplo.setComentario("OK");
        valoracionEjemplo.setReceta(recetaEjemplo);
        valoracionEjemplo.setUsuario(usuarioEjemplo);
    }

    @Test
    void testBuscarValoracionUsuario_CuandoExiste() {
        when(recetaValoracionRepository.findByRecetaIdAndUsuarioId(recetaIdEjemplo, usuarioIdEjemplo))
                .thenReturn(Optional.of(valoracionEjemplo));

        Optional<RecetaValoracion> resultado = recetaValoracionService.buscarValoracionUsuario(recetaIdEjemplo, usuarioIdEjemplo);

        assertTrue(resultado.isPresent());
        assertEquals(valoracionEjemplo.getId(), resultado.get().getId());
        assertEquals(valoracionEjemplo.getPuntuacion(), resultado.get().getPuntuacion());
        verify(recetaValoracionRepository, times(1)).findByRecetaIdAndUsuarioId(recetaIdEjemplo, usuarioIdEjemplo);
        verifyNoMoreInteractions(recetaValoracionRepository);
    }

    @Test
    void testEliminarValoracion() {
        long valoracionIdParaEliminar = 40L;
        doNothing().when(recetaValoracionRepository).deleteById(valoracionIdParaEliminar);

        recetaValoracionService.eliminarValoracion(valoracionIdParaEliminar);

        verify(recetaValoracionRepository, times(1)).deleteById(valoracionIdParaEliminar);
    }

    @Test
    void testValorarReceta_ActualizarExistente() {
        int nuevaPuntuacion = 5;
        String nuevoComentario = "¡Ahora excelente!";

        when(recetaValoracionRepository.findByRecetaIdAndUsuarioId(recetaIdEjemplo, usuarioIdEjemplo))
                .thenReturn(Optional.of(valoracionEjemplo));

        when(recetaValoracionRepository.save(any(RecetaValoracion.class))).thenAnswer(inv -> inv.getArgument(0));

        RecetaValoracion resultado = recetaValoracionService.valorarReceta(recetaIdEjemplo, usuarioIdEjemplo, nuevaPuntuacion, nuevoComentario);

        assertNotNull(resultado);
        assertEquals(valoracionEjemplo.getId(), resultado.getId()); 
        assertEquals(nuevaPuntuacion, resultado.getPuntuacion());
        assertEquals(nuevoComentario, resultado.getComentario());

        verify(recetaValoracionRepository, times(1)).findByRecetaIdAndUsuarioId(recetaIdEjemplo, usuarioIdEjemplo);
        verify(recetaValoracionRepository, times(1)).save(any(RecetaValoracion.class));
        verify(recetaRepository, never()).findById(anyLong());
        verify(usuarioRepository, never()).findById(anyLong());
    }

    @Test
    void testValorarReceta_PuntuacionInvalida_Menor() {
        int puntuacionInvalida = 0;
        assertThrows(IllegalArgumentException.class, () -> {
            recetaValoracionService.valorarReceta(recetaIdEjemplo, usuarioIdEjemplo, puntuacionInvalida, "Comentario");
        });
        verify(recetaValoracionRepository, never()).save(any()); 
    }

    @Test
    void testValorarReceta_PuntuacionInvalida_Mayor() {
        int puntuacionInvalida = 6;
        assertThrows(IllegalArgumentException.class, () -> {
            recetaValoracionService.valorarReceta(recetaIdEjemplo, usuarioIdEjemplo, puntuacionInvalida, "Comentario");
        });
        verify(recetaValoracionRepository, never()).save(any());
    }

    @Test
    void testValorarReceta_CrearNueva_RecetaNoEncontrada() {
        long recetaIdInexistente = 99L;
        int puntuacion = 4;
        when(recetaValoracionRepository.findByRecetaIdAndUsuarioId(recetaIdInexistente, usuarioIdEjemplo)).thenReturn(Optional.empty());
        when(recetaRepository.findById(recetaIdInexistente)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            recetaValoracionService.valorarReceta(recetaIdInexistente, usuarioIdEjemplo, puntuacion, "Comentario");
        });
        verify(recetaValoracionRepository, never()).save(any());
    }

    @Test
    void testValorarReceta_CrearNueva_UsuarioNoEncontrado() {
        long usuarioIdInexistente = 99L;
        int puntuacion = 4;
        when(recetaValoracionRepository.findByRecetaIdAndUsuarioId(recetaIdEjemplo, usuarioIdInexistente)).thenReturn(Optional.empty());
        when(recetaRepository.findById(recetaIdEjemplo)).thenReturn(Optional.of(new Receta())); 
        when(usuarioRepository.findById(usuarioIdInexistente)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            recetaValoracionService.valorarReceta(recetaIdEjemplo, usuarioIdInexistente, puntuacion, "Comentario");
        });
        verify(recetaValoracionRepository, never()).save(any());
    }

    @Test
    void testObtenerPuntuacionPromedio_ConValoraciones() {
        Double promedioEsperado = 4.5;
        when(recetaValoracionRepository.calcularPuntuacionPromedio(recetaIdEjemplo)).thenReturn(promedioEsperado);

        Double resultado = recetaValoracionService.obtenerPuntuacionPromedio(recetaIdEjemplo);

        assertEquals(promedioEsperado, resultado);
        verify(recetaValoracionRepository, times(1)).calcularPuntuacionPromedio(recetaIdEjemplo);
    }

    @Test
    void testObtenerPuntuacionPromedio_SinValoraciones() {
        when(recetaValoracionRepository.calcularPuntuacionPromedio(recetaIdEjemplo)).thenReturn(null); 

        Double resultado = recetaValoracionService.obtenerPuntuacionPromedio(recetaIdEjemplo);

        assertEquals(0.0, resultado, "Debería devolver 0.0 si no hay valoraciones");
        verify(recetaValoracionRepository, times(1)).calcularPuntuacionPromedio(recetaIdEjemplo);
    }

    @Test
    void testObtenerValoracionesPorReceta() {
        List<RecetaValoracion> listaValoraciones = Collections.singletonList(valoracionEjemplo);
        when(recetaValoracionRepository.findByRecetaId(recetaIdEjemplo)).thenReturn(listaValoraciones);

        List<RecetaValoracion> resultado = recetaValoracionService.obtenerValoracionesPorReceta(recetaIdEjemplo);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaValoracionRepository, times(1)).findByRecetaId(recetaIdEjemplo);
    }

    @Test
    void testObtenerDistribucionPuntuaciones() {
        Object[] dist5 = {5, 10L}; 
        Object[] dist4 = {4, 5L}; 
        List<Object[]> distribucionRepo = Arrays.asList(dist5, dist4);
        when(recetaValoracionRepository.getRatingDistribution(recetaIdEjemplo)).thenReturn(distribucionRepo);

        Map<Integer, Long> resultado = recetaValoracionService.obtenerDistribucionPuntuaciones(recetaIdEjemplo);

        assertNotNull(resultado);
        assertEquals(5, resultado.size()); 
        assertEquals(10L, resultado.get(5));
        assertEquals(5L, resultado.get(4));
        assertEquals(0L, resultado.get(3)); 
        assertEquals(0L, resultado.get(2));
        assertEquals(0L, resultado.get(1));
        verify(recetaValoracionRepository, times(1)).getRatingDistribution(recetaIdEjemplo);
    }

    @Test
    void testObtenerRecetasMejorValoradas() {
        Object[] recetaTop1 = {1L, 4.8, 20L}; 
        Object[] recetaTop2 = {5L, 4.5, 15L};
        List<Object[]> topRatedRepo = Arrays.asList(recetaTop1, recetaTop2);
        long minValoraciones = 10L;

        when(recetaValoracionRepository.findTopRatedRecipes(minValoraciones)).thenReturn(topRatedRepo);

        List<Object[]> resultado = recetaValoracionService.obtenerRecetasMejorValoradas(minValoraciones);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0)[0]); 
        verify(recetaValoracionRepository, times(1)).findTopRatedRecipes(minValoraciones);
    }

} 