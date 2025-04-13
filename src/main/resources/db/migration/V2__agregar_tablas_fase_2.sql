-- Tabla para videos de recetas
CREATE TABLE receta_videos (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    receta_id NUMBER NOT NULL,
    titulo VARCHAR2(255) NOT NULL,
    video_url VARCHAR2(500) NOT NULL,
    descripcion VARCHAR2(1000),
    duracion_segundos NUMBER,
    formato VARCHAR2(20),
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_video_receta FOREIGN KEY (receta_id) REFERENCES recetas(id)
);

-- Tabla para compartir recetas
CREATE TABLE receta_shares (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    receta_id NUMBER NOT NULL,
    usuario_id NUMBER NOT NULL,
    plataforma VARCHAR2(50) NOT NULL,
    fecha_compartida TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    enlace_generado VARCHAR2(500),
    clicks NUMBER DEFAULT 0,
    CONSTRAINT fk_share_receta FOREIGN KEY (receta_id) REFERENCES recetas(id),
    CONSTRAINT fk_share_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla para comentarios de recetas
CREATE TABLE receta_comentarios (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    receta_id NUMBER NOT NULL,
    usuario_id NUMBER NOT NULL,
    contenido VARCHAR2(2000) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    aprobado NUMBER(1) DEFAULT 0 NOT NULL,
    CONSTRAINT fk_comentario_receta FOREIGN KEY (receta_id) REFERENCES recetas(id),
    CONSTRAINT fk_comentario_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla para valoraciones de recetas
CREATE TABLE receta_valoraciones (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    receta_id NUMBER NOT NULL,
    usuario_id NUMBER NOT NULL,
    puntuacion NUMBER(1) NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
    comentario VARCHAR2(1000),
    fecha_valoracion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_valoracion_receta FOREIGN KEY (receta_id) REFERENCES recetas(id),
    CONSTRAINT fk_valoracion_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT uk_valoracion_usuario_receta UNIQUE (receta_id, usuario_id)
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_video_receta ON receta_videos(receta_id);
CREATE INDEX idx_share_receta ON receta_shares(receta_id);
CREATE INDEX idx_share_usuario ON receta_shares(usuario_id);
CREATE INDEX idx_share_plataforma ON receta_shares(plataforma);
CREATE INDEX idx_comentario_receta ON receta_comentarios(receta_id);
CREATE INDEX idx_comentario_usuario ON receta_comentarios(usuario_id);
CREATE INDEX idx_valoracion_receta ON receta_valoraciones(receta_id);
CREATE INDEX idx_valoracion_usuario ON receta_valoraciones(usuario_id);

-- Comentarios para documentación
COMMENT ON TABLE receta_videos IS 'Almacena los videos asociados a las recetas';
COMMENT ON TABLE receta_shares IS 'Registro de comparticiones de recetas en diferentes plataformas';
COMMENT ON TABLE receta_comentarios IS 'Comentarios de los usuarios sobre las recetas';
COMMENT ON TABLE receta_valoraciones IS 'Valoraciones (estrellas) de los usuarios para las recetas'; 