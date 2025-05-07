package com.example.proyect;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Configuration
public class DatabaseConnectionTest {

    @Bean
    @Profile("!test")
    public CommandLineRunner testDatabaseConnection(DataSource dataSource) {
        return args -> {
            System.out.println("======== PROBANDO CONEXIÓN A LA BASE DE DATOS ========");
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("URL de la base de datos: " + metaData.getURL());
                System.out.println("Usuario: " + metaData.getUserName());
                System.out.println("Nombre del producto: " + metaData.getDatabaseProductName());
                System.out.println("Versión del producto: " + metaData.getDatabaseProductVersion());
                System.out.println("JDBC Driver: " + metaData.getDriverName());
                System.out.println("JDBC Driver Version: " + metaData.getDriverVersion());
                System.out.println("¡Conexión exitosa a la base de datos!");
            } catch (Exception e) {
                System.err.println("Error al conectar a la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("======================================================");
        };
    }
} 