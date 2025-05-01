package com.example.proyect.service;

import com.example.proyect.model.Usuario;
import com.example.proyect.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder; // Asumiendo dependencia
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder; 

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setUsername("newuser");
        usuarioEjemplo.setPassword("plainPassword");
        usuarioEjemplo.setNombre("Nuevo");
        usuarioEjemplo.setApellido("Usuario");
        usuarioEjemplo.setEmail("new@example.com");
        usuarioEjemplo.setRol("ROLE_USER");
        usuarioEjemplo.setActivo(true);
    }

    @Test
    void testBuscarPorId_CuandoExiste() {
        // Arrange
        usuarioEjemplo.setPassword("encodedPassword"); 
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuarioEjemplo.getId(), resultado.get().getId());
        assertEquals(usuarioEjemplo.getUsername(), resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void testGuardarUsuario() {
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(usuarioEjemplo.getPassword())).thenReturn(encodedPassword);

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario userToSave = invocation.getArgument(0);
            userToSave.setPassword(encodedPassword); 
            if(userToSave.getId() == null) {
               userToSave.setId(2L); 
            }
            return userToSave;
        });

        Usuario usuarioGuardado = usuarioService.guardarUsuario(usuarioEjemplo);

        assertNotNull(usuarioGuardado);
        assertEquals(encodedPassword, usuarioGuardado.getPassword(), "La contraseña debería estar codificada");
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(usuarioRepository, times(1)).save(usuarioEjemplo); 
    }

    @Test
    void testEliminarUsuario() {
        long usuarioIdParaEliminar = 1L;
        doNothing().when(usuarioRepository).deleteById(usuarioIdParaEliminar);

        usuarioService.eliminarUsuario(usuarioIdParaEliminar);

        verify(usuarioRepository, times(1)).deleteById(usuarioIdParaEliminar);
    }

    @Test
    void testBuscarPorUsername_NoEncontrado() {
        String username = "usuarioInexistente";
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorUsername(username);

        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findByUsername(username);
    }

    @Test
    void testExisteUsername_SiExiste() {
        String username = "testuser";
        when(usuarioRepository.existsByUsername(username)).thenReturn(true);
        boolean resultado = usuarioService.existeUsername(username);
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).existsByUsername(username);
    }

    @Test
    void testExisteUsername_NoExiste() {
        String username = "nouser";
        when(usuarioRepository.existsByUsername(username)).thenReturn(false);
        boolean resultado = usuarioService.existeUsername(username);
        assertFalse(resultado);
        verify(usuarioRepository, times(1)).existsByUsername(username);
    }

    @Test
    void testExisteEmail_SiExiste() {
        String email = "test@example.com";
        when(usuarioRepository.existsByEmail(email)).thenReturn(true);
        boolean resultado = usuarioService.existeEmail(email);
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).existsByEmail(email);
    }

    @Test
    void testExisteEmail_NoExiste() {
        String email = "no@example.com";
        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        boolean resultado = usuarioService.existeEmail(email);
        assertFalse(resultado);
        verify(usuarioRepository, times(1)).existsByEmail(email);
    }

    @Test
    void testCrearUsuariosIniciales_CuandoYaExisten() {
        when(usuarioRepository.count()).thenReturn(3L); 

        usuarioService.crearUsuariosIniciales();

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(usuarioRepository, times(1)).count();
    }

    @Test
    void testContarUsuarios() {
        // Arrange
        long conteoEsperado = 25L;
        when(usuarioRepository.count()).thenReturn(conteoEsperado);

        // Act
        long resultado = usuarioService.contarUsuarios();

        // Assert
        assertEquals(conteoEsperado, resultado);
        verify(usuarioRepository, times(1)).count();
    }

    @Test
    void testActivarUsuario_UsuarioEncontrado() {
        // Arrange
        long usuarioId = 1L;
        usuarioEjemplo.setActivo(false); // Asegurar que empieza inactivo
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        usuarioService.activarUsuario(usuarioId);

        // Assert
        verify(usuarioRepository, times(1)).findById(usuarioId);
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().isActivo(), "El usuario debería estar activo");
    }

    @Test
    void testActivarUsuario_UsuarioNoEncontrado() {
        // Arrange
        long usuarioId = 99L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());
        // Act
        usuarioService.activarUsuario(usuarioId);
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, never()).save(any()); 
    }

    @Test
    void testDesactivarUsuario_UsuarioEncontrado() {
        // Arrange
        long usuarioId = 1L;
        usuarioEjemplo.setActivo(true); // Asegurar que empieza activo
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        usuarioService.desactivarUsuario(usuarioId);

        // Assert
        verify(usuarioRepository, times(1)).findById(usuarioId);
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(captor.capture());
        assertFalse(captor.getValue().isActivo(), "El usuario debería estar inactivo");
    }

    @Test
    void testDesactivarUsuario_UsuarioNoEncontrado() {
        // Arrange
        long usuarioId = 99L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());
        // Act
        usuarioService.desactivarUsuario(usuarioId);
        // Assert
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testResetearPassword_UsuarioEncontrado() {
        // Arrange
        long usuarioId = 1L;
        String nuevaPasswordPlana = "nuevaPass123";
        String nuevaPasswordCodificada = "encodedNuevaPass";

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.encode(nuevaPasswordPlana)).thenReturn(nuevaPasswordCodificada);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Usuario usuarioActualizado = usuarioService.resetearPassword(usuarioId, nuevaPasswordPlana);

        // Assert
        assertNotNull(usuarioActualizado);
        assertEquals(nuevaPasswordCodificada, usuarioActualizado.getPassword());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(passwordEncoder, times(1)).encode(nuevaPasswordPlana);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testResetearPassword_UsuarioNoEncontrado() {
        // Arrange
        long usuarioId = 99L;
        String nuevaPasswordPlana = "nuevaPass123";
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioService.resetearPassword(usuarioId, nuevaPasswordPlana);
        });
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testGuardarUsuario_NuevoUsuario() {
        // Arrange
        // ID es null (viene del setup)
        String plainPassword = usuarioEjemplo.getPassword();
        String encodedPassword = "encodedNewPassword";
        when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);
        // Simular guardado devolviendo el usuario con ID y contraseña codificada
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario userToSave = invocation.getArgument(0);
            userToSave.setId(2L); // Asignar ID simulado
            userToSave.setPassword(encodedPassword);
            return userToSave;
        });

        // Act
        Usuario usuarioGuardado = usuarioService.guardarUsuario(usuarioEjemplo);

        // Assert
        assertNotNull(usuarioGuardado);
        assertNotNull(usuarioGuardado.getId()); // Verificar que se asignó ID
        assertEquals(encodedPassword, usuarioGuardado.getPassword());
        verify(passwordEncoder, times(1)).encode(plainPassword);
        verify(usuarioRepository, times(1)).save(usuarioEjemplo);
    }

    @Test
    void testGuardarUsuario_ActualizarSinCambiarPassword() {
        long existingUserId = 3L;
        String existingEncodedPassword = "$2a$encodedExistingPassword"; 
        Usuario usuarioParaActualizar = new Usuario(); 
        usuarioParaActualizar.setId(existingUserId);
        usuarioParaActualizar.setUsername("existinguser");
        usuarioParaActualizar.setNombre("Nombre Actualizado");
        usuarioParaActualizar.setRol("ROLE_CHEF");
        usuarioParaActualizar.setPassword(existingEncodedPassword); 

        Usuario usuarioEnDB = new Usuario(); 
        usuarioEnDB.setId(existingUserId);
        usuarioEnDB.setUsername("existinguser");
        usuarioEnDB.setPassword(existingEncodedPassword);
        usuarioEnDB.setRol("ROLE_USER"); 

        // Simular que el usuario existe en la BD
        when(usuarioRepository.findById(existingUserId)).thenReturn(Optional.of(usuarioEnDB));
        // Simular el guardado (debería guardar el usuarioParaActualizar con la contraseña antigua)
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Usuario usuarioGuardado = usuarioService.guardarUsuario(usuarioParaActualizar);

        // Assert
        assertNotNull(usuarioGuardado);
        assertEquals(existingUserId, usuarioGuardado.getId());
        assertEquals(existingEncodedPassword, usuarioGuardado.getPassword(), "La contraseña no debería haber cambiado");
        assertEquals("Nombre Actualizado", usuarioGuardado.getNombre());
        assertEquals("ROLE_CHEF", usuarioGuardado.getRol()); 
        verify(usuarioRepository, times(1)).findById(existingUserId);
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, times(1)).save(usuarioParaActualizar);
    }

    // TODO: Añadir tests para métodos con Pageable y filtros
} 