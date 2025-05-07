package com.example.proyect.service;

import com.example.proyect.model.Usuario;
import com.example.proyect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private Environment env;
    
    private Random random = new Random();
    private Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    
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
            // Genera o lee contraseñas seguras
            String adminPass = getSecurePassword("usuario.admin.password", 12);
            String chefPass = getSecurePassword("usuario.chef.password", 12);
            String userPass = getSecurePassword("usuario.user.password", 12);
            
            // Usuario Administrador
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(adminPass));
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setEmail("admin@sistema.com");
            admin.setRol("ROLE_ADMIN");
            admin.setActivo(true);
            usuarioRepository.save(admin);
            
            // Usuario Chef
            Usuario chef = new Usuario();
            chef.setUsername("chef");
            chef.setPassword(passwordEncoder.encode(chefPass));
            chef.setNombre("Chef");
            chef.setApellido("Principal");
            chef.setEmail("chef@sistema.com");
            chef.setRol("ROLE_CHEF");
            chef.setActivo(true);
            usuarioRepository.save(chef);
            
            // Usuario Regular
            Usuario user = new Usuario();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode(userPass));
            user.setNombre("Usuario");
            user.setApellido("Regular");
            user.setEmail("user@sistema.com");
            user.setRol("ROLE_USER");
            user.setActivo(true);
            usuarioRepository.save(user);
            
            // Loguear las credenciales iniciales para que los admins puedan acceder la primera vez
            logger.info("CREDENCIALES INICIALES CREADAS:");
            logger.info("Admin - Usuario: admin | Contraseña: {}", adminPass);
            logger.info("Chef - Usuario: chef | Contraseña: {}", chefPass);
            logger.info("Usuario - Usuario: user | Contraseña: {}", userPass);
        }
    }
    
    /**
     * Obtiene una contraseña segura desde properties o genera una aleatoria
     * @param propertyName Nombre de la propiedad de configuración
     * @param length Longitud mínima de la contraseña
     * @return Contraseña segura
     */
    private String getSecurePassword(String propertyName, int length) {
        // Intentar obtener la contraseña desde las propiedades
        String password = env.getProperty(propertyName);
        
        // Si no hay propiedad definida, generar una contraseña aleatoria segura
        if (password == null || password.isEmpty()) {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            password = sb.toString();
        }
        
        return password;
    }

    /**
     * Cuenta el número total de usuarios
     */
    public long contarUsuarios() {
        return usuarioRepository.count();
    }
    
    /**
     * Lista los últimos N usuarios registrados
     */
    public List<Usuario> listarUltimosUsuarios(int cantidad) {
        Pageable pageable = PageRequest.of(0, cantidad);
        return usuarioRepository.findAll(pageable).getContent();
    }
    
    /**
     * Lista usuarios con filtros y paginación
     */
    public List<Usuario> listarUsuarios(String buscar, String rol, Boolean activo, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano);
        
        // Si no hay filtros, devolver todos
        if ((buscar == null || buscar.isEmpty()) && rol == null && activo == null) {
            return usuarioRepository.findAll(pageable).getContent();
        }
        
        // Con filtros
        return usuarioRepository.buscarConFiltros(buscar, rol, activo, pageable);
    }
    
    /**
     * Cuenta usuarios con filtros aplicados
     */
    public long contarUsuariosFiltrados(String buscar, String rol, Boolean activo) {
        // Si no hay filtros, contar todos
        if ((buscar == null || buscar.isEmpty()) && rol == null && activo == null) {
            return usuarioRepository.count();
        }
        
        // Con filtros
        return usuarioRepository.contarConFiltros(buscar, rol, activo);
    }
    
    /**
     * Guarda un usuario (nuevo o existente)
     */
    public Usuario guardarUsuario(Usuario usuario) {
        // Si el usuario es nuevo (no tiene ID) o ha cambiado la contraseña
        if (usuario.getId() == null || (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$"))) {
            // Codificar la contraseña antes de guardar
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            // Si estamos actualizando un usuario existente y no cambiamos la contraseña
            Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getId());
            if (usuarioExistente.isPresent()) {
                // Mantener la contraseña antigua
                usuario.setPassword(usuarioExistente.get().getPassword());
            }
        }
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Activa un usuario
     */
    public void activarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
        }
    }
    
    /**
     * Desactiva un usuario
     */
    public void desactivarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        }
    }
    
    /**
     * Elimina un usuario
     */
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    /**
     * Resetea la contraseña de un usuario
     * @param id ID del usuario
     * @param password Nueva contraseña
     * @return Usuario actualizado
     */
    public Usuario resetearPassword(Long id, String password) {
        Usuario usuario = buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Encriptar la nueva contraseña
        String passwordEncriptado = passwordEncoder.encode(password);
        usuario.setPassword(passwordEncriptado);
        
        return usuarioRepository.save(usuario);
    }
} 