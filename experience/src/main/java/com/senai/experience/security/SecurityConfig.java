package com.senai.experience.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Deprecated
@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable())
            .authorizeHttpRequests(auth -> auth
                // Rotas públicas
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario").permitAll()

                // IoT: apenas POST em endpoints de fabricação
                .requestMatchers(HttpMethod.POST, "/api/fabricacao/**").hasRole("IOT")
                
                // Status de fabricação: leitura para autenticados, escrita apenas para IOT e ADMIN
                .requestMatchers(HttpMethod.GET, "/api/veiculo/*/status").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/veiculo/*/status").hasAnyRole("IOT", "ADMIN")

                // Admin: acesso total ao painel admin
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Cliente: apenas os próprios pedidos
                .requestMatchers("/api/pedido/meus-pedidos/**").hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN")
                // Vendedor e Admin: acesso a todos os pedidos
                .requestMatchers("/api/pedido/**").hasAnyRole("VENDEDOR", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/produto/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/produto/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.PUT, "/api/produto/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.DELETE, "/api/produto/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
