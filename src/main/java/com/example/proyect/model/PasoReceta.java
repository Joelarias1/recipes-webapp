package com.example.proyect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pasos_receta")
public class PasoReceta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;
    
    @Column(nullable = false)
    private Integer numeroOrden;
    
    @Column(nullable = false, length = 1000)
    private String descripcion;
    
    private String imagenUrl;
} 