# Recipes Web App

Aplicación web para gestión de recetas de cocina.

## Requisitos

- Java 17
- Spring Boot 3.4.4
- Oracle Database

## Configuración de Seguridad

### Gestión de Tokens y Credenciales

#### SonarQube

Para ejecutar análisis de SonarQube sin exponer tokens sensibles:

1. Nunca incluyas tokens de SonarQube en archivos que sean versionados (como pom.xml).
2. Usa una de estas alternativas seguras:

   a. Variables de entorno:
   ```bash
   export SONAR_TOKEN=tu_token_aqui
   ```

   b. Parámetro en la línea de comandos:
   ```bash
   mvn clean verify sonar:sonar -Dsonar.token=tu_token_aqui
   ```

   c. Archivo sonar-project.properties local (añadido al .gitignore):
   ```
   # Copia sonar-project.properties.template a sonar-project.properties
   # y añade tu token
   sonar.token=tu_token_aqui
   ```

3. Revocar y rotar regularmente los tokens de SonarQube:
   - Accede a SonarQube > Mi Cuenta > Tokens de Seguridad
   - Revoca los tokens antiguos o comprometidos
   - Genera nuevos tokens según sea necesario

#### Usuarios Iniciales

Las contraseñas para los usuarios iniciales se configuran en application.properties.
En entornos de producción, estas deben ser:

1. Cambiadas tras la primera ejecución
2. Definidas mediante variables de entorno en lugar de archivos de propiedades

## Ejecutar la Aplicación

1. Configura la base de datos y las credenciales.
2. Ejecuta la aplicación:
   ```
   ./mvnw spring-boot:run
   ```

## Ejecutar Análisis de SonarQube

```bash
./mvnw clean verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=TU_TOKEN
```

Para entornos CI/CD, configura variables de entorno secretas para el token. 