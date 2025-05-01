package com.example.proyect.service;

import com.example.proyect.model.Receta;
import com.example.proyect.model.Usuario; 
import com.example.proyect.repository.IngredienteRepository;
import com.example.proyect.repository.PasoRecetaRepository;
import com.example.proyect.repository.RecetaRepository;
import com.example.proyect.repository.UsuarioRepository;
import com.example.proyect.repository.RecetaIngredienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.proyect.model.Ingrediente;
import com.example.proyect.model.RecetaIngrediente;
import org.mockito.ArgumentCaptor;
import com.example.proyect.model.PasoReceta;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private UsuarioRepository usuarioRepository; 

    @Mock
    private IngredienteRepository ingredienteRepository; 

    @Mock
    private PasoRecetaRepository pasoRecetaRepository; 

    @Mock
    private RecetaIngredienteRepository recetaIngredienteRepository;

    @InjectMocks
    private RecetaService recetaService;

    private Receta recetaEjemplo;
    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setUsername("testuser");

        recetaEjemplo = new Receta();
        recetaEjemplo.setId(10L);
        recetaEjemplo.setNombre("Tarta de Manzana Test");
        recetaEjemplo.setDescripcion("Una deliciosa tarta");
        recetaEjemplo.setCreador(usuarioEjemplo); 
        recetaEjemplo.setPublica(true);
    }

    @Test
    void testBuscarPorId_CuandoExiste() {
        when(recetaRepository.findById(10L)).thenReturn(Optional.of(recetaEjemplo));

        Optional<Receta> resultado = recetaService.buscarPorId(10L);

        assertTrue(resultado.isPresent(), "El Optional no debería estar vacío");
        assertEquals(recetaEjemplo.getId(), resultado.get().getId(), "El ID de la receta encontrada no coincide");
        assertEquals(recetaEjemplo.getNombre(), resultado.get().getNombre(), "El nombre de la receta encontrada no coincide");

        verify(recetaRepository, times(1)).findById(10L);
        verifyNoMoreInteractions(recetaRepository);
    }

    @Test
    void testBuscarPorId_CuandoNoExiste() {
        when(recetaRepository.findById(anyLong())).thenReturn(Optional.empty()); 

        Optional<Receta> resultado = recetaService.buscarPorId(99L); 

        assertFalse(resultado.isPresent(), "El Optional debería estar vacío cuando la receta no existe");

        verify(recetaRepository, times(1)).findById(99L);
    }

    @Test
    void testGuardarReceta() {
        when(recetaRepository.save(any(Receta.class))).thenReturn(recetaEjemplo);

        // Act
        Receta recetaGuardada = recetaService.guardarReceta(recetaEjemplo);

        assertNotNull(recetaGuardada);
        assertEquals(recetaEjemplo.getId(), recetaGuardada.getId());
        assertEquals(recetaEjemplo.getNombre(), recetaGuardada.getNombre());
        verify(recetaRepository, times(1)).save(recetaEjemplo);
    }

    @Test
    void testEliminarReceta() {
        // Arrange
        long recetaIdParaEliminar = 10L;
        doNothing().when(pasoRecetaRepository).deleteByRecetaId(anyLong());
        doNothing().when(recetaIngredienteRepository).deleteByRecetaId(anyLong());
        doNothing().when(recetaRepository).deleteById(anyLong());

        // Act
        recetaService.eliminarReceta(recetaIdParaEliminar);

        verify(pasoRecetaRepository, times(1)).deleteByRecetaId(recetaIdParaEliminar);
        verify(recetaIngredienteRepository, times(1)).deleteByRecetaId(recetaIdParaEliminar);
        verify(recetaRepository, times(1)).deleteById(recetaIdParaEliminar);
    }

    @Test
    void testListarRecetasPublicas() {
        Receta recetaPublica1 = new Receta(); 
        recetaPublica1.setId(11L);
        recetaPublica1.setNombre("Receta Pública 1");
        recetaPublica1.setPublica(true);

        Receta recetaPublica2 = new Receta();
        recetaPublica2.setId(12L);
        recetaPublica2.setNombre("Receta Pública 2");
        recetaPublica2.setPublica(true);

        List<Receta> listaPublica = Arrays.asList(recetaPublica1, recetaPublica2);

        when(recetaRepository.findByPublicaTrue()).thenReturn(listaPublica);

        List<Receta> resultado = recetaService.listarRecetasPublicas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).isPublica());
        assertTrue(resultado.get(1).isPublica());
        verify(recetaRepository, times(1)).findByPublicaTrue();
    }

    @Test
    void testAgregarIngredienteAReceta_Exitoso() {
        // Arrange
        long recetaId = 10L;
        long ingredienteId = 50L;
        String cantidad = "200";
        String unidad = "gr";

        Ingrediente ingredienteEjemplo = new Ingrediente();
        ingredienteEjemplo.setId(ingredienteId);
        ingredienteEjemplo.setNombre("Harina");

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));
        when(ingredienteRepository.findById(ingredienteId)).thenReturn(Optional.of(ingredienteEjemplo));

        when(recetaIngredienteRepository.save(any(RecetaIngrediente.class)))
                .thenAnswer(invocation -> {
                    RecetaIngrediente ri = invocation.getArgument(0);
                    ri.setId(100L); 
                    return ri;
                });

        // Act
        RecetaIngrediente resultado = recetaService.agregarIngredienteAReceta(recetaId, ingredienteId, cantidad, unidad);

        assertNotNull(resultado);
        assertNotNull(resultado.getId(), "El ID de RecetaIngrediente no debería ser null después de guardar");
        assertEquals(recetaId, resultado.getReceta().getId());
        assertEquals(ingredienteId, resultado.getIngrediente().getId());
        assertEquals(cantidad, resultado.getCantidad());
        assertEquals(unidad, resultado.getUnidad());

        verify(recetaRepository, times(1)).findById(recetaId);
        verify(ingredienteRepository, times(1)).findById(ingredienteId);
        ArgumentCaptor<RecetaIngrediente> captor = ArgumentCaptor.forClass(RecetaIngrediente.class);
        verify(recetaIngredienteRepository, times(1)).save(captor.capture());
        assertEquals(recetaEjemplo, captor.getValue().getReceta());
        assertEquals(ingredienteEjemplo, captor.getValue().getIngrediente());
    }

    @Test
    void testAgregarIngredienteAReceta_RecetaNoEncontrada() {
        // Arrange
        long recetaIdInexistente = 99L;
        long ingredienteId = 50L;
        String cantidad = "100";
        String unidad = "gr";

        when(recetaRepository.findById(recetaIdInexistente)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            recetaService.agregarIngredienteAReceta(recetaIdInexistente, ingredienteId, cantidad, unidad);
        }, "Debería lanzar RuntimeException si la receta no existe");

        verify(ingredienteRepository, never()).findById(anyLong());
        verify(recetaIngredienteRepository, never()).save(any());
    }

    @Test
    void testAgregarIngredienteAReceta_IngredienteNoEncontrado() {
        long recetaId = 10L;
        long ingredienteIdInexistente = 99L;
        String cantidad = "100";
        String unidad = "gr";

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));
        when(ingredienteRepository.findById(ingredienteIdInexistente)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            recetaService.agregarIngredienteAReceta(recetaId, ingredienteIdInexistente, cantidad, unidad);
        }, "Debería lanzar RuntimeException si el ingrediente no existe");

        verify(recetaRepository, times(1)).findById(recetaId);
        verify(recetaIngredienteRepository, never()).save(any());
    }

    @Test
    void testAgregarPasoAReceta_Exitoso() {
        long recetaId = 10L;
        int numeroOrden = 1;
        String descripcion = "Mezclar ingredientes";
        String imagenUrl = "http://example.com/paso1.jpg";

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));

        when(pasoRecetaRepository.save(any(PasoReceta.class)))
                .thenAnswer(invocation -> {
                    PasoReceta paso = invocation.getArgument(0);
                    paso.setId(200L); 
                    return paso;
                });

        // Act
        PasoReceta resultado = recetaService.agregarPasoAReceta(recetaId, numeroOrden, descripcion, imagenUrl);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(recetaId, resultado.getReceta().getId());
        assertEquals(numeroOrden, resultado.getNumeroOrden());
        assertEquals(descripcion, resultado.getDescripcion());
        assertEquals(imagenUrl, resultado.getImagenUrl());

        // Verificar llamadas
        verify(recetaRepository, times(1)).findById(recetaId);
        ArgumentCaptor<PasoReceta> captor = ArgumentCaptor.forClass(PasoReceta.class);
        verify(pasoRecetaRepository, times(1)).save(captor.capture());
        assertEquals(recetaEjemplo, captor.getValue().getReceta());
    }

    @Test
    void testAgregarPasoAReceta_RecetaNoEncontrada() {
        long recetaIdInexistente = 99L;
        int numeroOrden = 1;
        String descripcion = "Paso inválido";
        String imagenUrl = null;

        when(recetaRepository.findById(recetaIdInexistente)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            recetaService.agregarPasoAReceta(recetaIdInexistente, numeroOrden, descripcion, imagenUrl);
        }, "Debería lanzar RuntimeException si la receta no existe");

        verify(pasoRecetaRepository, never()).save(any());
    }

    @Test
    void testEsCreadorOAdmin_CuandoEsCreador() {
        long recetaId = 10L; 
        String usernameCreador = "testuser";

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));

        boolean resultado = recetaService.esCreadorOAdmin(recetaId, usernameCreador);

        assertTrue(resultado, "Debería devolver true si el usuario es el creador");
        verify(recetaRepository, times(1)).findById(recetaId);
        verify(usuarioRepository, never()).findByUsername(anyString());
    }

    @Test
    void testEsCreadorOAdmin_CuandoEsAdmin() {
        // Arrange
        long recetaId = 10L;
        String usernameAdmin = "adminUser";
        Usuario adminUser = new Usuario();
        adminUser.setId(2L);
        adminUser.setUsername(usernameAdmin);
        adminUser.setRol("ROLE_ADMIN"); // Rol de administrador

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo)); 
        when(usuarioRepository.findByUsername(usernameAdmin)).thenReturn(Optional.of(adminUser));

        // Act
        boolean resultado = recetaService.esCreadorOAdmin(recetaId, usernameAdmin);

        // Assert
        assertTrue(resultado, "Debería devolver true si el usuario es admin");
        verify(recetaRepository, times(1)).findById(recetaId);
        verify(usuarioRepository, times(1)).findByUsername(usernameAdmin);
    }

    @Test
    void testEsCreadorOAdmin_CuandoNoEsNinguno() {
        // Arrange
        long recetaId = 10L;
        String usernameOtro = "otroUser";
        Usuario otroUser = new Usuario();
        otroUser.setId(3L);
        otroUser.setUsername(usernameOtro);
        otroUser.setRol("ROLE_USER"); // Rol normal

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo)); // Creada por "testuser"
        when(usuarioRepository.findByUsername(usernameOtro)).thenReturn(Optional.of(otroUser));

        // Act
        boolean resultado = recetaService.esCreadorOAdmin(recetaId, usernameOtro);

        // Assert
        assertFalse(resultado, "Debería devolver false si no es creador ni admin");
        verify(recetaRepository, times(1)).findById(recetaId);
        verify(usuarioRepository, times(1)).findByUsername(usernameOtro);
    }

    @Test
    void testEsCreadorOAdmin_RecetaNoEncontrada() {
        // Arrange
        long recetaIdInexistente = 99L;
        String username = "testuser";

        when(recetaRepository.findById(recetaIdInexistente)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            recetaService.esCreadorOAdmin(recetaIdInexistente, username);
        }, "Debería lanzar RuntimeException si la receta no existe");

        // Verificar que no se intentó buscar al usuario
        verify(usuarioRepository, never()).findByUsername(anyString());
    }

    @Test
    void testEsCreadorOAdmin_UsuarioNoEncontrado() {
        long recetaId = 10L; 
        String usernameInexistente = "nouser";

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(recetaEjemplo));
        when(usuarioRepository.findByUsername(usernameInexistente)).thenReturn(Optional.empty());

        boolean resultado = recetaService.esCreadorOAdmin(recetaId, usernameInexistente);

        assertFalse(resultado, "Debería devolver false si no es creador y el usuario no se encuentra");
        verify(recetaRepository, times(1)).findById(recetaId);
        verify(usuarioRepository, times(1)).findByUsername(usernameInexistente);
    }

    @Test
    void testListarRecetasRecientes() {
        // Arrange
        Receta recetaReciente1 = new Receta();
        recetaReciente1.setId(21L); // ID diferente
        recetaReciente1.setNombre("Receta Reciente 1");
        recetaReciente1.setPublica(true);

        Receta recetaReciente2 = new Receta();
        recetaReciente2.setId(22L);
        recetaReciente2.setNombre("Receta Reciente 2");
        recetaReciente2.setPublica(true);

        List<Receta> listaReciente = Arrays.asList(recetaReciente1, recetaReciente2);
        when(recetaRepository.findRecentPublicRecipes()).thenReturn(listaReciente);

        // Act
        List<Receta> resultado = recetaService.listarRecetasRecientes();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(recetaRepository, times(1)).findRecentPublicRecipes();
    }

    @Test
    void testBuscarRecetasPorNombre() {
        // Arrange
        String terminoBusqueda = "Tarta";
        Receta recetaEncontrada = new Receta();
        recetaEncontrada.setId(10L);
        recetaEncontrada.setNombre("Tarta de Manzana Test");
        recetaEncontrada.setPublica(true);

        List<Receta> listaEncontrada = Arrays.asList(recetaEncontrada);
        when(recetaRepository.findByNombreContainingIgnoreCaseAndPublicaTrue(terminoBusqueda))
                .thenReturn(listaEncontrada);

        // Act
        List<Receta> resultado = recetaService.buscarRecetasPorNombre(terminoBusqueda);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(recetaEncontrada.getNombre(), resultado.get(0).getNombre());
        verify(recetaRepository, times(1)).findByNombreContainingIgnoreCaseAndPublicaTrue(terminoBusqueda);
    }

    @Test
    void testBuscarRecetasPorNombre_NoEncontrado() {
        // Arrange
        String terminoBusqueda = "Inexistente";
        when(recetaRepository.findByNombreContainingIgnoreCaseAndPublicaTrue(terminoBusqueda))
                .thenReturn(Collections.emptyList()); // Devolver lista vacía

        // Act
        List<Receta> resultado = recetaService.buscarRecetasPorNombre(terminoBusqueda);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty(), "La lista debería estar vacía si no se encuentra nada");
        verify(recetaRepository, times(1)).findByNombreContainingIgnoreCaseAndPublicaTrue(terminoBusqueda);
    }

    @Test
    void testBuscarRecetasPorDificultad() {
        // Arrange
        String dificultad = "Fácil";
        Receta recetaFacil = new Receta();
        recetaFacil.setId(10L);
        recetaFacil.setNombre("Receta Fácil");
        recetaFacil.setDificultad(dificultad);
        recetaFacil.setPublica(true);

        List<Receta> listaEncontrada = Arrays.asList(recetaFacil);
        when(recetaRepository.findByDificultadAndPublicaTrue(dificultad)).thenReturn(listaEncontrada);

        // Act
        List<Receta> resultado = recetaService.buscarRecetasPorDificultad(dificultad);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(dificultad, resultado.get(0).getDificultad());
        verify(recetaRepository, times(1)).findByDificultadAndPublicaTrue(dificultad);
    }

    @Test
    void testBuscarRecetasPorDificultad_NoEncontrado() {
        // Arrange
        String dificultad = "Imposible";
        when(recetaRepository.findByDificultadAndPublicaTrue(dificultad))
                .thenReturn(Collections.emptyList()); // Devolver lista vacía

        // Act
        List<Receta> resultado = recetaService.buscarRecetasPorDificultad(dificultad);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(recetaRepository, times(1)).findByDificultadAndPublicaTrue(dificultad);
    }

    @Test
    void testListarRecetasPorUsuario() {
        // Arrange
        Long usuarioId = 1L;
        List<Receta> listaUsuario = Arrays.asList(recetaEjemplo);
        when(recetaRepository.findByCreadorId(usuarioId)).thenReturn(listaUsuario);

        // Act
        List<Receta> resultado = recetaService.listarRecetasPorUsuario(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuarioId, resultado.get(0).getCreador().getId());
        verify(recetaRepository, times(1)).findByCreadorId(usuarioId);
    }

    @Test
    void testListarRecetasPorUsuario_SinRecetas() {
        // Arrange
        Long usuarioId = 2L; // Un usuario diferente al del setup
        when(recetaRepository.findByCreadorId(usuarioId)).thenReturn(Collections.emptyList());

        // Act
        List<Receta> resultado = recetaService.listarRecetasPorUsuario(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(recetaRepository, times(1)).findByCreadorId(usuarioId);
    }

    @Test
    void testListarTodasLasRecetas() {
        Receta recetaPrivada = new Receta(); 
        recetaPrivada.setId(30L);
        recetaPrivada.setNombre("Receta Privada");
        recetaPrivada.setPublica(false);

        List<Receta> todasLasRecetas = Arrays.asList(recetaEjemplo, recetaPrivada);
        when(recetaRepository.findAll()).thenReturn(todasLasRecetas);

        // Act
        List<Receta> resultado = recetaService.listarTodasLasRecetas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(recetaRepository, times(1)).findAll();
    }

    @Test
    void testListarIngredientesDisponibles() {
        // Arrange
        Ingrediente ing1 = new Ingrediente();
        ing1.setId(1L); ing1.setNombre("Harina");
        Ingrediente ing2 = new Ingrediente();
        ing2.setId(2L); ing2.setNombre("Azúcar");

        List<Ingrediente> listaIngredientes = Arrays.asList(ing1, ing2);
        when(ingredienteRepository.findAll()).thenReturn(listaIngredientes);

        // Act
        List<Ingrediente> resultado = recetaService.listarIngredientesDisponibles();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(ingredienteRepository, times(1)).findAll();
    }

    @Test
    void testContarRecetas() {
        // Arrange
        long conteoEsperado = 5L;
        when(recetaRepository.count()).thenReturn(conteoEsperado);

        // Act
        long resultado = recetaService.contarRecetas();

        // Assert
        assertEquals(conteoEsperado, resultado);
        verify(recetaRepository, times(1)).count();
    }

    @Test
    void testListarIngredientesPorReceta() {
        // Arrange
        long recetaId = 10L;
        Ingrediente ing1 = new Ingrediente(); ing1.setId(1L);
        Ingrediente ing2 = new Ingrediente(); ing2.setId(2L);
        RecetaIngrediente ri1 = new RecetaIngrediente();
        ri1.setReceta(recetaEjemplo); ri1.setIngrediente(ing1); ri1.setCantidad("100"); ri1.setUnidad("gr");
        RecetaIngrediente ri2 = new RecetaIngrediente();
        ri2.setReceta(recetaEjemplo); ri2.setIngrediente(ing2); ri2.setCantidad("1 Taza");

        List<RecetaIngrediente> listaRI = Arrays.asList(ri1, ri2);
        when(recetaIngredienteRepository.findByRecetaId(recetaId)).thenReturn(listaRI);

        // Act
        List<RecetaIngrediente> resultado = recetaService.listarIngredientesPorReceta(recetaId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(recetaIngredienteRepository, times(1)).findByRecetaId(recetaId);
    }

    @Test
    void testListarIngredientesPorReceta_SinIngredientes() {
        long recetaId = 11L; 
        when(recetaIngredienteRepository.findByRecetaId(recetaId)).thenReturn(Collections.emptyList());

        // Act
        List<RecetaIngrediente> resultado = recetaService.listarIngredientesPorReceta(recetaId);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(recetaIngredienteRepository, times(1)).findByRecetaId(recetaId);
    }

    @Test
    void testListarPasosPorReceta() {
        // Arrange
        long recetaId = 10L;
        PasoReceta paso1 = new PasoReceta();
        paso1.setReceta(recetaEjemplo); paso1.setNumeroOrden(1); paso1.setDescripcion("Paso 1");
        PasoReceta paso2 = new PasoReceta();
        paso2.setReceta(recetaEjemplo); paso2.setNumeroOrden(2); paso2.setDescripcion("Paso 2");

        List<PasoReceta> listaPasos = Arrays.asList(paso1, paso2);
        when(pasoRecetaRepository.findByRecetaIdOrderByNumeroOrden(recetaId)).thenReturn(listaPasos);

        // Act
        List<PasoReceta> resultado = recetaService.listarPasosPorReceta(recetaId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(pasoRecetaRepository, times(1)).findByRecetaIdOrderByNumeroOrden(recetaId);
    }

    @Test
    void testListarPasosPorReceta_SinPasos() {
        // Arrange
        long recetaId = 12L; // ID de receta diferente
        when(pasoRecetaRepository.findByRecetaIdOrderByNumeroOrden(recetaId)).thenReturn(Collections.emptyList());

        // Act
        List<PasoReceta> resultado = recetaService.listarPasosPorReceta(recetaId);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pasoRecetaRepository, times(1)).findByRecetaIdOrderByNumeroOrden(recetaId);
    }

    @Test
    void testEliminarIngredienteDeReceta() {
        // Arrange
        long recetaId = 10L;
        long recetaIngredienteId = 150L; 
        doNothing().when(recetaIngredienteRepository).deleteByRecetaIdAndId(anyLong(), anyLong());

        // Act
        recetaService.eliminarIngredienteDeReceta(recetaId, recetaIngredienteId);

        // Assert
        verify(recetaIngredienteRepository, times(1)).deleteByRecetaIdAndId(recetaId, recetaIngredienteId);
    }

    @Test
    void testEliminarPasoDeReceta() {
        long recetaId = 10L;
        long pasoId = 250L; 
        doNothing().when(pasoRecetaRepository).deleteByRecetaIdAndId(anyLong(), anyLong());

        recetaService.eliminarPasoDeReceta(recetaId, pasoId);

        verify(pasoRecetaRepository, times(1)).deleteByRecetaIdAndId(recetaId, pasoId);
    }

} 