package com.example.proyect.repository;

import com.example.proyect.model.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    /**
     * Busca usuarios aplicando filtros opcionales (nombre/username/email, rol y estado activo)
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "(:buscar IS NULL OR :buscar = '' OR " +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :buscar, '%'))) " +
           "AND (:rol IS NULL OR u.rol = :rol) " +
           "AND (:activo IS NULL OR u.activo = :activo)")
    List<Usuario> buscarConFiltros(
            @Param("buscar") String buscar,
            @Param("rol") String rol,
            @Param("activo") Boolean activo,
            Pageable pageable);
    
    /**
     * Cuenta usuarios aplicando filtros opcionales
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE " +
           "(:buscar IS NULL OR :buscar = '' OR " +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :buscar, '%'))) " +
           "AND (:rol IS NULL OR u.rol = :rol) " +
           "AND (:activo IS NULL OR u.activo = :activo)")
    long contarConFiltros(
            @Param("buscar") String buscar,
            @Param("rol") String rol,
            @Param("activo") Boolean activo);
} 