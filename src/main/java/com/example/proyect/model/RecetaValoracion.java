package com.example.proyect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "receta_valoraciones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"receta_id", "usuario_id"})  // Un usuario solo puede valorar una receta una vez
})
public class RecetaValoracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuario que valoró la receta
    
    @Column(nullable = false)
    private Integer puntuacion; // 1-5 estrellas
    
    @Column(length = 1000)
    private String comentario; // Comentario opcional junto con la valoración
    
    @Column(nullable = false)
    private LocalDateTime fechaValoracion;
    
    @PrePersist
    protected void onCreate() {
        fechaValoracion = LocalDateTime.now();
    }
} 