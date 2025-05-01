-- Esquema inicial de la base de datos

-- Tabla de usuarios
CREATE TABLE usuarios (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    nombre VARCHAR2(100) NOT NULL,
    apellido VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) NOT NULL UNIQUE,
    rol VARCHAR2(20) NOT NULL,
    activo NUMBER(1) DEFAULT 1
);

-- Tabla de recetas
CREATE TABLE recetas (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR2(255) NOT NULL,
    descripcion VARCHAR2(1000),
    tiempo_preparacion NUMBER,
    dificultad VARCHAR2(20),
    imagen_url VARCHAR2(500),
    publica NUMBER(1) DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    usuario_id NUMBER NOT NULL,
    CONSTRAINT fk_receta_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla de ingredientes
CREATE TABLE ingredientes (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR2(255) NOT NULL UNIQUE,
    descripcion VARCHAR2(500)
);

-- Tabla de relación receta-ingredientes
CREATE TABLE receta_ingredientes (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    receta_id NUMBER NOT NULL,
    ingrediente_id NUMBER NOT NULL,
    cantidad VARCHAR2(50) NOT NULL,
    unidad VARCHAR2(50),
    CONSTRAINT fk_receta_ingrediente_receta FOREIGN KEY (receta_id) REFERENCES recetas(id),
    CONSTRAINT fk_receta_ingrediente_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id)
);

-- Tabla de pasos de receta
CREATE TABLE pasos_receta (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    receta_id NUMBER NOT NULL,
    numero_orden NUMBER NOT NULL,
    descripcion VARCHAR2(1000) NOT NULL,
    imagen_url VARCHAR2(500),
    CONSTRAINT fk_paso_receta FOREIGN KEY (receta_id) REFERENCES recetas(id)
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_receta_usuario ON recetas(usuario_id);
CREATE INDEX idx_receta_publica ON recetas(publica);
CREATE INDEX idx_receta_ingrediente_receta ON receta_ingredientes(receta_id);
CREATE INDEX idx_receta_ingrediente_ingrediente ON receta_ingredientes(ingrediente_id);
CREATE INDEX idx_paso_receta ON pasos_receta(receta_id);
CREATE INDEX idx_paso_orden ON pasos_receta(numero_orden); 