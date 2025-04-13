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
@Table(name = "receta_videos")
public class RecetaVideo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(nullable = false)
    private String videoUrl; // URL al video (YouTube, Vimeo, etc.) o ruta al archivo
    
    private String descripcion;
    
    private Integer duracionSegundos; // Duración del video en segundos
    
    private String formato; // mp4, webm, etc.
    
    @Column(nullable = false)
    private LocalDateTime fechaSubida;
    
    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }
} 