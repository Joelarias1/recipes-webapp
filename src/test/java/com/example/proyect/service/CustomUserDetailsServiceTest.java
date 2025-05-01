package com.example.proyect.service;

import com.example.proyect.model.Usuario;
import com.example.proyect.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setUsername("testuser");
        usuarioEjemplo.setPassword("password"); // La contraseña real no importa mucho aquí
        usuarioEjemplo.setNombre("Test");
        usuarioEjemplo.setApellido("User");
        usuarioEjemplo.setEmail("test@example.com");
        usuarioEjemplo.setRol("ROLE_USER");
        usuarioEjemplo.setActivo(true);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        // Arrange
        String username = "testuser";
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuarioEjemplo));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(usuarioEjemplo.getUsername(), userDetails.getUsername());
        // Spring Security añade "ROLE_" automáticamente si no está, asegúrate de que tu rol lo tenga o ajusta aquí
        assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(usuarioRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String username = "unknownuser";
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        }, "Debería lanzar UsernameNotFoundException si el usuario no existe");

        verify(usuarioRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(usuarioRepository);
    }
} 