package com.Gdev.pos_lite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Páginas/recursos estáticos públicos
                        .requestMatchers("/", "/index.html", "/scanner.html",
                                "/static/**", "/webjars/**", "/favicon.ico").permitAll()
                        // La API sí requiere autenticación
                        .requestMatchers("/api/**").authenticated()
                        // cualquier otra ruta que sirvas
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
