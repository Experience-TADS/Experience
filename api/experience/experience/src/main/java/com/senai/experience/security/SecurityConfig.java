package com.senai.experience.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    // Origens permitidas — lidas do application.properties / variável de ambiente AWS
    @Value("${cors.allowed.origins:http://localhost:3000,http://localhost:5173,http://localhost:8081}")
    private String corsAllowedOrigins;

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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers.frameOptions().disable())
            .authorizeHttpRequests(auth -> auth
                // Rotas públicas
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/pessoaFisica").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/pessoaJuridica").permitAll()

                // IoT: apenas POST em endpoints de fabricação
                .requestMatchers(HttpMethod.POST, "/api/fabricacao/**").hasRole("IOT")
                
                // Node-RED: endpoint público para receber eventos do ESP32 via MQTT
                .requestMatchers(HttpMethod.POST, "/api/veiculo/nodered/evento").permitAll()

                // Status de fabricação: leitura para autenticados, escrita apenas para IOT e ADMIN
                .requestMatchers(HttpMethod.GET, "/api/veiculo/*/status").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/veiculo/*/status").permitAll()

                // Admin: acesso total ao painel admin
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Dashboard do app mobile: apenas para clientes autenticados
                .requestMatchers(HttpMethod.GET, "/api/dashboard").hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN")

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
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        // Origens lidas do application.properties — suporta localhost (dev) e AWS Amplify (produção)
        // Em produção: CORS_ALLOWED_ORIGINS=https://app.experience.com,https://main.d1xyz.amplifyapp.com
        List<String> origins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // cache do preflight por 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
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
