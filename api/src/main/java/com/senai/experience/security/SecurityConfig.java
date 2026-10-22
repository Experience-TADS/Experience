package com.senai.experience.security;

import java.util.List;

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
    private final TokenBlacklist tokenBlacklist;
    private final AuditAccessDeniedHandler auditAccessDeniedHandler;
    private final StatusUnauthorizedEntryPoint statusUnauthorizedEntryPoint;
    private final LoginRateLimitFilter loginRateLimitFilter;

    public SecurityConfig(UserDetailsService userDetailsService,
                          TokenBlacklist tokenBlacklist,
                          AuditAccessDeniedHandler auditAccessDeniedHandler,
                          StatusUnauthorizedEntryPoint statusUnauthorizedEntryPoint,
                          LoginRateLimitFilter loginRateLimitFilter) {
        this.userDetailsService = userDetailsService;
        this.tokenBlacklist = tokenBlacklist;
        this.auditAccessDeniedHandler = auditAccessDeniedHandler;
        this.statusUnauthorizedEntryPoint = statusUnauthorizedEntryPoint;
        this.loginRateLimitFilter = loginRateLimitFilter;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(userDetailsService, tokenBlacklist);
    }

    /**
     * Cadeia dedicada ao endpoint POST /api/veiculo/{id}/status (card de autorização de status).
     *
     * <p>Fica com @Order(1) para ser avaliada antes da cadeia principal e capturar apenas
     * esse path/método via securityMatcher. É a única cadeia com entry point de 401, de modo
     * que requisições sem token nessa rota recebem 401 — enquanto todas as demais rotas, na
     * cadeia principal, mantêm o 403 padrão da API para anônimos. Mantém o mesmo stack de
     * filtros (JWT + rate limit) e os cabeçalhos de segurança da cadeia principal.</p>
     */
    @Bean
    @org.springframework.core.annotation.Order(1)
    public SecurityFilterChain statusSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new org.springframework.security.web.util.matcher.AntPathRequestMatcher(
                    "/api/veiculo/*/status", HttpMethod.POST.name()))
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(ct -> {})
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000))
                .xssProtection(xss -> xss.headerValue(
                    org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .referrerPolicy(referrer -> referrer
                    .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; frame-ancestors 'none'"))
            )
            .authorizeHttpRequests(auth -> auth
                .anyRequest().hasAnyRole("ADMIN", "VENDEDOR"))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 401 para anônimo nesta rota; 403 (accessDenied) para token sem a role exigida.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(statusUnauthorizedEntryPoint)
                .accessDeniedHandler(auditAccessDeniedHandler))
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @org.springframework.core.annotation.Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // S-009: cabeçalhos de segurança HTTP
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(ct -> {})                          // X-Content-Type-Options: nosniff
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000))
                .xssProtection(xss -> xss.headerValue(
                    org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .referrerPolicy(referrer -> referrer
                    .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; frame-ancestors 'none'"))
            )
            .authorizeHttpRequests(auth -> auth
                // Rotas públicas
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario/logout").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/pessoaFisica").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/pessoaJuridica").permitAll()

                // IoT: apenas POST em endpoints de fabricação
                .requestMatchers(HttpMethod.POST, "/api/fabricacao/**").hasRole("IOT")
                
                // Node-RED: endpoint público para receber eventos do ESP32 via MQTT
                .requestMatchers(HttpMethod.POST, "/api/veiculo/nodered/evento").permitAll()

                // Veiculo
                .requestMatchers(HttpMethod.POST, "/api/veiculo/*/confirmacao-chegada").hasAnyRole("VENDEDOR", "ADMIN")

                // Status de fabricação: leitura para autenticados, escrita apenas para IOT e ADMIN
                .requestMatchers(HttpMethod.GET, "/api/veiculo/*/status").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/veiculo/*/status").hasAnyRole("ADMIN", "VENDEDOR")

                .requestMatchers(HttpMethod.POST, "/api/analytics/sessao").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/analytics/sessao/resumo").hasRole("ADMIN")

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
            // S-010: registra acessos negados (403) no audit.log.
            // Mantém o comportamento padrão da API: 403 para requisições anônimas.
            // O requisito de 401 sem token no endpoint POST /api/veiculo/*/status é
            // tratado de forma isolada por uma SecurityFilterChain dedicada (ver abaixo),
            // para não alterar o 403 global das demais rotas.
            .exceptionHandling(ex -> ex.accessDeniedHandler(auditAccessDeniedHandler))
            // S-008: rate limiting do login — roda antes do filtro JWT.
            // Como ambos usam addFilterBefore na mesma âncora, o JWT é registrado
            // por último para ficar imediatamente antes do UsernamePasswordAuthenticationFilter,
            // e o rate limit (registrado antes) roda à frente do JWT.
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(loginRateLimitFilter, JwtAuthFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        //ordem: localhost, métodos, labels e retorno

            config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-type"));
            config.setAllowCredentials(true);

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
