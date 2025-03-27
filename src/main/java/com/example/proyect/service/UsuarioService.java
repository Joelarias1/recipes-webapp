package com.example.proyect.service;

import com.example.proyect.model.Usuario;
import com.example.proyect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public Usuario guardar(Usuario usuario) {
        // Encriptamos la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
    
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }
    
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    public void crearUsuariosIniciales() {
        // Solo crear usuarios si no existen
        if (usuarioRepository.count() == 0) {
            // Usuario Administrador
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setEmail("admin@sistema.com");
            admin.setRol("ROLE_ADMIN");
            admin.setActivo(true);
            usuarioRepository.save(admin);
            
            // Usuario Chef
            Usuario chef = new Usuario();
            chef.setUsername("chef");
            chef.setPassword(passwordEncoder.encode("chef123"));
            chef.setNombre("Chef");
            chef.setApellido("Principal");
            chef.setEmail("chef@sistema.com");
            chef.setRol("ROLE_CHEF");
            chef.setActivo(true);
            usuarioRepository.save(chef);
            
            // Usuario Regular
            Usuario user = new Usuario();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setNombre("Usuario");
            user.setApellido("Regular");
            user.setEmail("user@sistema.com");
            user.setRol("ROLE_USER");
            user.setActivo(true);
            usuarioRepository.save(user);
        }
    }
} 