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
@Table(name = "receta_shares")
public class RecetaShare {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuario que compartió la receta
    
    @Column(nullable = false)
    private String plataforma; // "FACEBOOK", "TWITTER", "INSTAGRAM", "EMAIL", "LINK"
    
    @Column(nullable = false)
    private LocalDateTime fechaCompartida;
    
    private String enlaceGenerado; // URL generada para compartir
    
    private Integer clicks = 0; // Número de clicks en el enlace
    
    @PrePersist
    protected void onCreate() {
        fechaCompartida = LocalDateTime.now();
    }
} 