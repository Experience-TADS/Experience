# Checklist — Liberação da API para Testes
## O que falta após implementar os Cards 1-6

> **Contexto:** Cards 1-6 referem-se a:
> - Card 1-5: Estrutura base, autenticação, CRUD de usuários, endereços, telefones, pedidos
> - Card 6: Documentação de arquitetura
>
> **Estado atual identificado:**
> - ✅ UserRole enum criado
> - ✅ Campo `role` adicionado em Usuario
> - ✅ DTOs de request/response criados
> - ✅ SecurityConfig com proteção por roles
> - ✅ JwtUtil gerando token com role
> - ❌ CustomUserDetailsService ainda usa `.roles("USER")` hardcoded
> - ❌ Sem tratamento global de exceções
> - ❌ Sem documentação Swagger/OpenAPI

---

## 🔴 BLOQUEADORES — Impedem testes básicos

### BLOQ-01: CustomUserDetailsService não usa o role real do banco
**Arquivo:** `security/CustomUserDetailsService.java` (linha 31)

**Problema:**
```java
.roles("USER") // ← hardcoded, ignora o role do banco
```

**Solução:**
```java
@Override
public UserDetails loadUserByUsername(String emailUsuario) {
    Usuario usuario = usuarioRepository.findByEmail(emailUsuario);
    if (usuario == null) {
        throw new UsernameNotFoundException("Usuário não encontrado: " + emailUsuario);
    }

    // Usar o role real do banco, com fallback seguro
    String role = usuario.getRole() != null
            ? usuario.getRole().name()          // ex: "ROLE_CLIENTE"
            : "ROLE_CLIENTE";                   // fallback padrão

    return org.springframework.security.core.userdetails.User.builder()
            .username(usuario.getEmail())
            .password(usuario.getSenhaHash())
            .authorities(role)                  // authorities em vez de roles()
            .build();
}
```

**Por que é bloqueador:** Sem isso, todos os usuários têm role "USER" e o SecurityConfig
rejeita todas as requisições protegidas (403 Forbidden).

**Estimativa:** 5 minutos

---

### BLOQ-02: Sem tratamento global de exceções
**Problema:** Quando um recurso não é encontrado, a API retorna 500 ou null em vez de 404 com mensagem clara.

**Solução:** Criar `exception/GlobalExceptionHandler.java`:

```java
package com.senai.experience.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Usuário não encontrado",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado",
                "Você não tem permissão para acessar este recurso"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

E criar `exception/ErrorResponse.java`:

```java
package com.senai.experience.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
}
```

**Por que é bloqueador:** Sem isso, erros retornam stack traces ou respostas inconsistentes,
dificultando o debug no frontend.

**Estimativa:** 15 minutos

---

## 🟡 IMPORTANTES — Melhoram a experiência de testes

### IMP-01: Documentação Swagger/OpenAPI
**Problema:** Testadores precisam adivinhar os contratos da API.

**Solução:**

1. Adicionar no `pom.xml`:
```xml
<!-- Swagger/OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

2. Criar `config/OpenApiConfig.java`:
```java
package com.senai.experience.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Toyota Experience API")
                        .version("1.0")
                        .description("API para acompanhamento de fabricação de veículos")
                        .contact(new Contact()
                                .name("Equipe SENAI")
                                .email("contato@senai.br")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
```

3. Liberar o Swagger no `SecurityConfig`:
```java
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
```

**Acesso:** `http://localhost:8080/swagger-ui.html`

**Estimativa:** 20 minutos

---

### IMP-02: Endpoint de health check
**Problema:** Não há forma simples de verificar se a API está no ar.

**Solução:** Criar `controllers/HealthController.java`:

```java
package com.senai.experience.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "Toyota Experience API"
        );
    }
}
```

E liberar no `SecurityConfig`:
```java
.requestMatchers("/health").permitAll()
```

**Estimativa:** 5 minutos

---

### IMP-03: CORS configurado
**Problema:** Frontend rodando em `localhost:3000` não consegue chamar a API em `localhost:8080`.

**Solução:** Criar `config/CorsConfig.java`:

```java
package com.senai.experience.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

**Estimativa:** 10 minutos

---

### IMP-04: Dados de teste (seed)
**Problema:** Banco vazio dificulta testes.

**Solução:** Criar `config/DataSeeder.java`:

```java
package com.senai.experience.config;

import com.senai.experience.entities.Usuario;
import com.senai.experience.entities.PessoaFisica;
import com.senai.experience.entities.role.UserRole;
import com.senai.experience.repositories.UsuarioRepository;
import com.senai.experience.repositories.PessoaFisicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository,
            PessoaFisicaRepository pessoaFisicaRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // Verificar se já existe dados
            if (usuarioRepository.count() > 0) {
                return;
            }

            // Criar usuário admin
            PessoaFisica admin = new PessoaFisica();
            admin.setNome("Admin Sistema");
            admin.setEmail("admin@toyota.com");
            admin.setSenhaHash(passwordEncoder.encode("admin123"));
            admin.setDataNascimento(LocalDate.of(1990, 1, 1));
            admin.setCpf("12345678901");
            admin.setRole(UserRole.ROLE_ADMIN);
            pessoaFisicaRepository.save(admin);

            // Criar vendedor
            PessoaFisica vendedor = new PessoaFisica();
            vendedor.setNome("João Vendedor");
            vendedor.setEmail("vendedor@toyota.com");
            vendedor.setSenhaHash(passwordEncoder.encode("vendedor123"));
            vendedor.setDataNascimento(LocalDate.of(1985, 5, 15));
            vendedor.setCpf("98765432100");
            vendedor.setRole(UserRole.ROLE_VENDEDOR);
            pessoaFisicaRepository.save(vendedor);

            // Criar cliente
            PessoaFisica cliente = new PessoaFisica();
            cliente.setNome("Maria Cliente");
            cliente.setEmail("cliente@gmail.com");
            cliente.setSenhaHash(passwordEncoder.encode("cliente123"));
            cliente.setDataNascimento(LocalDate.of(1995, 8, 20));
            cliente.setCpf("11122233344");
            cliente.setRole(UserRole.ROLE_CLIENTE);
            pessoaFisicaRepository.save(cliente);

            System.out.println("✅ Dados de teste criados:");
            System.out.println("   Admin:    admin@toyota.com / admin123");
            System.out.println("   Vendedor: vendedor@toyota.com / vendedor123");
            System.out.println("   Cliente:  cliente@gmail.com / cliente123");
        };
    }
}
```

**Estimativa:** 15 minutos

---

## 🟢 OPCIONAIS — Facilitam desenvolvimento futuro

### OPC-01: Logs estruturados
Adicionar `@Slf4j` do Lombok nos controllers e logar requisições importantes:

```java
@Slf4j
@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("Tentativa de login: {}", loginRequest.getEmail());
        // ...
    }
}
```

**Estimativa:** 10 minutos

---

### OPC-02: Profiles do Spring
Separar configurações de dev/prod no `application.properties`:

```properties
# application.properties (comum)
spring.application.name=experience
spring.profiles.active=dev

# application-dev.properties
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# application-prod.properties
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=validate
```

**Estimativa:** 10 minutos

---

## 📋 Checklist de Liberação

### Bloqueadores (obrigatórios)
```
[ ] BLOQ-01 — CustomUserDetailsService usando role real do banco
[ ] BLOQ-02 — GlobalExceptionHandler criado
```

### Importantes (recomendados)
```
[ ] IMP-01 — Swagger/OpenAPI configurado
[ ] IMP-02 — Endpoint /health criado
[ ] IMP-03 — CORS configurado para localhost:3000
[ ] IMP-04 — Dados de teste (seed) criados
```

### Opcionais (nice to have)
```
[ ] OPC-01 — Logs estruturados com @Slf4j
[ ] OPC-02 — Profiles dev/prod separados
```

---

## 🧪 Roteiro de Testes Pós-Liberação

### 1. Verificar se a API sobe
```bash
mvn spring-boot:run
```
Deve subir sem erros e logar "Started ExperienceApplication".

### 2. Testar health check
```bash
curl http://localhost:8080/health
```
Deve retornar `{"status":"UP", ...}`.

### 3. Testar login com dados de seed
```bash
curl -X POST http://localhost:8080/api/usuario/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@toyota.com","senha":"admin123"}'
```
Deve retornar `{"token":"eyJ...","email":"admin@toyota.com","role":"ROLE_ADMIN"}`.

### 4. Testar rota protegida com token
```bash
TOKEN="<token_do_passo_3>"
curl http://localhost:8080/api/usuario/me \
  -H "Authorization: Bearer $TOKEN"
```
Deve retornar os dados do usuário admin.

### 5. Testar acesso negado (403)
```bash
# Tentar acessar rota de admin com token de cliente
curl http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer <token_de_cliente>"
```
Deve retornar 403 com mensagem clara.

### 6. Acessar Swagger UI
Abrir no navegador: `http://localhost:8080/swagger-ui.html`

---

## ⏱️ Estimativa Total

| Categoria | Tempo |
|-----------|-------|
| Bloqueadores | 20 min |
| Importantes | 50 min |
| Opcionais | 20 min |
| **Total mínimo** | **20 min** |
| **Total recomendado** | **70 min** |
| **Total completo** | **90 min** |

---

## 🚀 Ordem de Execução Recomendada

```
1. BLOQ-01 (5min)  → Corrigir CustomUserDetailsService
2. BLOQ-02 (15min) → Criar GlobalExceptionHandler
3. IMP-04 (15min)  → Criar DataSeeder
4. IMP-02 (5min)   → Criar HealthController
5. IMP-03 (10min)  → Configurar CORS
6. IMP-01 (20min)  → Configurar Swagger
7. Testar tudo     → Seguir roteiro de testes
```

**Após os passos 1-4, a API já está funcional para testes básicos.**
