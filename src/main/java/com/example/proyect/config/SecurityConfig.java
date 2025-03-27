package com.example.proyect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions().disable())
            .authorizeHttpRequests(authz -> authz
                // Recursos públicos
                .requestMatchers("/", "/index", "/home", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/error", "/error/**").permitAll()
                
                // Acceso a recetas públicas
                .requestMatchers("/recetas/buscar", "/recetas/listar").permitAll()
                .requestMatchers("/recetas/detalle/**").authenticated()
                
                // Rutas de administración
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/admin/dashboard").hasRole("ADMIN")
                .requestMatchers("/admin/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/admin/recetas/**").hasRole("ADMIN")
                .requestMatchers("/admin/categorias/**").hasRole("ADMIN")
                .requestMatchers("/admin/ingredientes/**").hasRole("ADMIN")
                .requestMatchers("/admin/configuracion/**").hasRole("ADMIN")
                
                // Rutas específicas para roles
                .requestMatchers("/chef/**").hasRole("CHEF")
                .requestMatchers("/usuario/**").hasAnyRole("USER", "CHEF", "ADMIN")
                
                // Consola H2 para desarrollo
                .requestMatchers("/h2-console/**").permitAll()
                
                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
            
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 