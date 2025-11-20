package com.medpro.medpro.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Desabilita a proteção CSRF. É seguro para APIs REST que usam JWT,
                // e é necessário para o seu frontend HTML funcionar com POST/PUT.
                .csrf(csrf -> csrf.disable())

                // Define que a aplicação não usará sessão (é stateless)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define a política de autorização:
                .authorizeHttpRequests(req -> {
                    // Permite acesso público a TODAS as requisições (APENAS PARA DESENVOLVIMENTO)
                    req.anyRequest().permitAll();
                })
                .build();
    }
}