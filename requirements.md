# Requerimientos del Sistema de Recetas

## Frontend

### Tecnologías Requeridas
- Spring Boot
- Spring Security
- Thymeleaf
- CSS para estilos (Tailwind)

### Protección de URLs
- Implementar separación entre URLs públicas y privadas
- Configurar Spring Security adecuadamente

### Sistema de Autenticación
- Página de login
- Mínimo 3 usuarios con diferentes roles
- Gestión de sesiones

### Páginas Públicas
1. **Página de Inicio**
   - Mostrar recetas recientes
   - Mostrar recetas populares
   - Banners comerciales
   - Acceso al login
   - Diseño responsive

2. **Búsqueda de Recetas**
   - Por nombre


### Páginas Privadas
1. **Visualización Detallada de Recetas**
   - Ingredientes completos
   - Instrucciones paso a paso

## Backend

### Tecnologías
- Spring Boot
- Oracle Cloud Database
- Spring Data JPA
- Spring Security

### Seguridad (OWASP Top 10)
1. Broken Access Control
   - Implementar control de acceso basado en roles
   - Validar permisos en cada endpoint

2. Cryptographic Failures
   - Encriptar contraseñas
   - Usar HTTPS
   - Proteger datos sensibles

3. Injection
   - Usar JPA/Hibernate para prevenir SQL Injection
   - Validar y sanitizar inputs

4. Insecure Design
   - Implementar rate limiting
   - Validación de datos
   - Manejo de errores apropiado

5. Security Misconfiguration
   - Configurar headers de seguridad
   - Deshabilitar características no utilizadas
   - Usar configuraciones seguras por defecto

6. Vulnerable Components
   - Mantener dependencias actualizadas
   - Usar versiones estables

7. Authentication Failures
   - Implementar bloqueo de cuentas
   - Políticas de contraseñas fuertes
   - Manejo seguro de sesiones

8. Software and Data Integrity Failures
   - Validar integridad de datos
   - Verificar fuentes confiables

9. Security Logging & Monitoring
   - Implementar logs de seguridad
   - Monitorear actividades sospechosas

10. Server-Side Request Forgery
    - Validar URLs

### Base de Datos
- Conexión a Oracle Cloud mediante wallet
- Diseño de esquema relacional
- Optimización de consultas
- Manejo de transacciones

1. **users**
   - Gestión de usuarios y roles
   - Información de autenticación

2. **recipes**
   - Información básica de recetas
   - Metadatos (tiempo, dificultad, etc.)

3. **ingredients**
   - Catálogo de ingredientes
   - Información nutricional

4. **recipe_ingredients**
   - Relación recetas-ingredientes
   - Cantidades y unidades

5. **categories**
   - Tipos de cocina
   - Clasificaciones de recetas

6. **countries**
   - Países de origen
   - Regiones culinarias

7. **recipe_steps**
   - Instrucciones paso a paso
   - Orden de preparación

8. **recipe_images**
   - Galería de fotos
   - Metadatos de imágenes

9. **ratings**
   - Valoraciones de usuarios
   - Comentarios 