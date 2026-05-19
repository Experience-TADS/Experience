# Cards de Correção — Liberação da API
## Issues prioritárias para testes

> **Contexto:** Após implementar os Cards 1-6 (estrutura base + autenticação + CRUD),
> estas correções são necessárias para liberar a API para testes funcionais.

---

## 🔴 CARD BLOQ-01 — Corrigir CustomUserDetailsService

**Tipo:** Bug Crítico  
**Prioridade:** BLOQUEADOR  
**Estimativa:** 5 minutos  
**Depende de:** Nenhuma

---

**Como** desenvolvedor  
**Quero** que o CustomUserDetailsService use o role real do banco  
**Para que** o Spring Security aplique as regras de autorização corretamente

---

### Problema Atual

**Arquivo:** `security/CustomUserDetailsService.java` (linha 31)

```java
return org.springframework.security.core.userdetails.User.builder()
        .username(usuario.getEmail())
        .password(usuario.getSenhaHash())
        .roles("USER") // ← PROBLEMA: hardcoded, ignora o role do banco
        .build();
```

**Impacto:** Todos os usuários têm role "USER", fazendo com que o `SecurityConfig`
rejeite todas as requisições protegidas com 403 Forbidden, mesmo com token válido.

---

### Solução

Substituir o método `loadUserByUsername` completo:

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

**Atenção:** Usar `.authorities(role)` em vez de `.roles("USER")`.  
O método `.roles()` adiciona o prefixo "ROLE_" automaticamente, mas nosso enum
já tem o prefixo, então usamos `.authorities()` diretamente.

---

### Critérios de Aceite

- [ ] Método `loadUserByUsername` atualizado
- [ ] Usa `usuario.getRole().name()` em vez de hardcoded
- [ ] Usa `.authorities(role)` em vez de `.roles("USER")`
- [ ] Tem fallback para `ROLE_CLIENTE` se role for null
- [ ] Aplicação compila sem erros
- [ ] Login retorna token válido
- [ ] Token permite acesso a rotas do role correspondente

---

### Como Testar

1. Subir a aplicação
2. Fazer login com um usuário que tem `role = ROLE_ADMIN`
3. Usar o token para acessar uma rota protegida com `hasRole("ADMIN")`
4. Deve retornar 200 OK em vez de 403 Forbidden

---
---

## 🔴 CARD BLOQ-02 — Criar Tratamento Global de Exceções

**Tipo:** Feature Crítica  
**Prioridade:** BLOQUEADOR  
**Estimativa:** 15 minutos  
**Depende de:** Nenhuma

---

**Como** desenvolvedor  
**Quero** que a API retorne erros padronizados e claros  
**Para que** o frontend possa exibir mensagens amigáveis ao usuário

---

### Problema Atual

Quando ocorre um erro, a API retorna:
- Stack traces completos (expõe estrutura interna)
- Status 500 para erros que deveriam ser 404 ou 400
- Respostas inconsistentes (às vezes JSON, às vezes texto)
- Null em vez de mensagem de erro

---

### Solução

**Passo 1:** Criar `exception/ErrorResponse.java`

```java
package com.senai.experience.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
}
```

---

**Passo 2:** Criar `exception/GlobalExceptionHandler.java`

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

    // 404 - Usuário não encontrado
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

    // 403 - Acesso negado
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

    // 400 - Erros de validação (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    // 400 - Argumentos inválidos
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

    // 500 - Erro genérico (fallback)
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

---

### Critérios de Aceite

- [ ] Classe `ErrorResponse` criada em `exception/`
- [ ] Classe `GlobalExceptionHandler` criada em `exception/`
- [ ] Anotação `@RestControllerAdvice` presente
- [ ] Trata `UsernameNotFoundException` → 404
- [ ] Trata `AccessDeniedException` → 403
- [ ] Trata `MethodArgumentNotValidException` → 400 com campos
- [ ] Trata `IllegalArgumentException` → 400
- [ ] Trata `Exception` genérica → 500
- [ ] Aplicação compila sem erros

---

### Como Testar

**Teste 1 — 404 Usuário não encontrado:**
```bash
curl -X POST http://localhost:8080/api/usuario/login \
  -H "Content-Type: application/json" \
  -d '{"email":"naoexiste@teste.com","senha":"123"}'
```
Deve retornar:
```json
{
  "timestamp": "2026-04-10T15:30:00",
  "status": 404,
  "error": "Usuário não encontrado",
  "message": "Usuário não encontrado: naoexiste@teste.com"
}
```

**Teste 2 — 403 Acesso negado:**
```bash
# Login como cliente
TOKEN_CLIENTE="<token_de_cliente>"

# Tentar acessar rota de admin
curl http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer $TOKEN_CLIENTE"
```
Deve retornar:
```json
{
  "timestamp": "2026-04-10T15:31:00",
  "status": 403,
  "error": "Acesso negado",
  "message": "Você não tem permissão para acessar este recurso"
}
```

**Teste 3 — 400 Validação:**
```bash
curl -X POST http://localhost:8080/api/usuario \
  -H "Content-Type: application/json" \
  -d '{"email":"invalido","senha":"12"}'
```
Deve retornar:
```json
{
  "email": "deve ser um endereço de e-mail válido",
  "senha": "A senha deve conter no mínimo 6 caracteres"
}
```

---
---

## 🟡 CARD IMP-01 — Configurar Swagger/OpenAPI

**Tipo:** Documentação  
**Prioridade:** IMPORTANTE  
**Estimativa:** 20 minutos  
**Depende de:** Nenhuma

---

**Como** testador  
**Quero** acessar uma documentação interativa da API  
**Para que** possa testar os endpoints sem precisar usar curl ou Postman

---

### Solução

**Passo 1:** Adicionar dependência no `pom.xml` (após as dependências de JWT):

```xml
<!-- Swagger/OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

---

**Passo 2:** Criar `config/OpenApiConfig.java`

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
                        .version("1.0.0")
                        .description("API para acompanhamento de fabricação e entrega de veículos Toyota")
                        .contact(new Contact()
                                .name("Equipe SENAI")
                                .email("contato@senai.br")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT obtido no endpoint /api/usuario/login")));
    }
}
```

---

**Passo 3:** Liberar o Swagger no `SecurityConfig.java`

Adicionar antes de `.anyRequest().authenticated()`:

```java
// Swagger UI
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
```

O bloco completo fica:

```java
.authorizeHttpRequests(auth -> auth
    // Rotas públicas
    .requestMatchers("/h2-console/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/usuario/login").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/usuario").permitAll()
    
    // Swagger UI
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
    
    // ... resto das regras
    .anyRequest().authenticated()
)
```

---

### Critérios de Aceite

- [ ] Dependência `springdoc-openapi-starter-webmvc-ui` adicionada no `pom.xml`
- [ ] Classe `OpenApiConfig` criada em `config/`
- [ ] Bean `customOpenAPI()` configurado com título, versão e contato
- [ ] Esquema de segurança JWT configurado
- [ ] Rotas do Swagger liberadas no `SecurityConfig`
- [ ] Aplicação compila sem erros
- [ ] `mvn clean install` executa com sucesso

---

### Como Testar

1. Subir a aplicação: `mvn spring-boot:run`
2. Abrir no navegador: `http://localhost:8080/swagger-ui.html`
3. Deve exibir a interface do Swagger com todos os endpoints
4. Clicar em "Authorize" no canto superior direito
5. Inserir o token JWT (obtido do `/api/usuario/login`)
6. Testar um endpoint protegido diretamente pelo Swagger

---
---

## 🟡 CARD IMP-02 — Criar Endpoint de Health Check

**Tipo:** Infraestrutura  
**Prioridade:** IMPORTANTE  
**Estimativa:** 5 minutos  
**Depende de:** Nenhuma

---

**Como** DevOps  
**Quero** um endpoint simples para verificar se a API está no ar  
**Para que** possa monitorar a saúde do serviço

---

### Solução

**Passo 1:** Criar `controllers/HealthController.java`

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
                "service", "Toyota Experience API",
                "version", "1.0.0"
        );
    }
}
```

---

**Passo 2:** Liberar no `SecurityConfig.java`

Adicionar antes de `.anyRequest().authenticated()`:

```java
// Health check
.requestMatchers("/health").permitAll()
```

---

### Critérios de Aceite

- [ ] Classe `HealthController` criada
- [ ] Endpoint `GET /health` implementado
- [ ] Retorna JSON com status, timestamp, service e version
- [ ] Rota `/health` liberada no `SecurityConfig`
- [ ] Aplicação compila sem erros

---

### Como Testar

```bash
curl http://localhost:8080/health
```

Deve retornar:
```json
{
  "status": "UP",
  "timestamp": "2026-04-10T15:45:00.123",
  "service": "Toyota Experience API",
  "version": "1.0.0"
}
```

---
---

## 🟡 CARD IMP-03 — Configurar CORS

**Tipo:** Infraestrutura  
**Prioridade:** IMPORTANTE  
**Estimativa:** 10 minutos  
**Depende de:** Nenhuma

---

**Como** desenvolvedor frontend  
**Quero** que a API aceite requisições do localhost:3000  
**Para que** possa testar a integração frontend-backend localmente

---

### Problema Atual

Quando o frontend (Next.js em `localhost:3000`) tenta chamar a API (`localhost:8080`),
o navegador bloqueia a requisição com erro CORS:

```
Access to fetch at 'http://localhost:8080/api/usuario/login' from origin 
'http://localhost:3000' has been blocked by CORS policy
```

---

### Solução

Criar `config/CorsConfig.java`

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
        
        // Permitir credenciais (cookies, Authorization header)
        config.setAllowCredentials(true);
        
        // Origens permitidas (frontend local)
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001"
        ));
        
        // Headers permitidos
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Aplicar configuração a todas as rotas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
```

---

### Critérios de Aceite

- [ ] Classe `CorsConfig` criada em `config/`
- [ ] Bean `corsFilter()` configurado
- [ ] Permite `localhost:3000` e `localhost:3001`
- [ ] Permite todos os headers (`*`)
- [ ] Permite métodos GET, POST, PUT, DELETE, OPTIONS, PATCH
- [ ] `setAllowCredentials(true)` habilitado
- [ ] Aplicação compila sem erros

---

### Como Testar

**Teste 1 — Via navegador:**
1. Abrir o console do navegador (F12)
2. Executar:
```javascript
fetch('http://localhost:8080/health')
  .then(r => r.json())
  .then(console.log)
```
3. Deve retornar o JSON sem erro de CORS

**Teste 2 — Via frontend Next.js:**
1. No frontend, fazer uma chamada à API:
```typescript
const response = await fetch('http://localhost:8080/api/usuario/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'teste@teste.com', senha: '123456' })
});
```
2. Não deve aparecer erro de CORS no console

---
---

## 🟡 CARD IMP-04 — Criar Dados de Teste (Seed)

**Tipo:** Infraestrutura  
**Prioridade:** IMPORTANTE  
**Estimativa:** 15 minutos  
**Depende de:** BLOQ-01 (role no banco)

---

**Como** testador  
**Quero** que o banco tenha dados iniciais ao subir a aplicação  
**Para que** possa testar os endpoints sem precisar cadastrar usuários manualmente

---

### Solução

Criar `config/DataSeeder.java`

```java
package com.senai.experience.config;

import com.senai.experience.entities.PessoaFisica;
import com.senai.experience.entities.role.UserRole;
import com.senai.experience.repositories.PessoaFisicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            PessoaFisicaRepository pessoaFisicaRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // Verificar se já existe dados
            if (pessoaFisicaRepository.count() > 0) {
                log.info("Banco já possui dados. Seed ignorado.");
                return;
            }

            log.info("Criando dados de teste...");

            // Admin
            PessoaFisica admin = new PessoaFisica();
            admin.setNome("Admin Sistema");
            admin.setEmail("admin@toyota.com");
            admin.setSenhaHash(passwordEncoder.encode("admin123"));
            admin.setDataNascimento(LocalDate.of(1990, 1, 1));
            admin.setCpf("12345678901");
            admin.setRole(UserRole.ROLE_ADMIN);
            pessoaFisicaRepository.save(admin);

            // Vendedor
            PessoaFisica vendedor = new PessoaFisica();
            vendedor.setNome("João Vendedor");
            vendedor.setEmail("vendedor@toyota.com");
            vendedor.setSenhaHash(passwordEncoder.encode("vendedor123"));
            vendedor.setDataNascimento(LocalDate.of(1985, 5, 15));
            vendedor.setCpf("98765432100");
            vendedor.setRole(UserRole.ROLE_VENDEDOR);
            pessoaFisicaRepository.save(vendedor);

            // Cliente
            PessoaFisica cliente = new PessoaFisica();
            cliente.setNome("Maria Cliente");
            cliente.setEmail("cliente@gmail.com");
            cliente.setSenhaHash(passwordEncoder.encode("cliente123"));
            cliente.setDataNascimento(LocalDate.of(1995, 8, 20));
            cliente.setCpf("11122233344");
            cliente.setRole(UserRole.ROLE_CLIENTE);
            pessoaFisicaRepository.save(cliente);

            log.info("✅ Dados de teste criados:");
            log.info("   Admin:    admin@toyota.com / admin123");
            log.info("   Vendedor: vendedor@toyota.com / vendedor123");
            log.info("   Cliente:  cliente@gmail.com / cliente123");
        };
    }
}
```

---

### Critérios de Aceite

- [ ] Classe `DataSeeder` criada em `config/`
- [ ] Bean `CommandLineRunner` implementado
- [ ] Verifica se o banco já tem dados antes de inserir
- [ ] Cria 3 usuários: admin, vendedor e cliente
- [ ] Senhas são criptografadas com `BCryptPasswordEncoder`
- [ ] Cada usuário tem role correto
- [ ] Logs informativos são exibidos no console
- [ ] Aplicação compila sem erros

---

### Como Testar

1. Limpar o banco de dados (dropar todas as tabelas)
2. Subir a aplicação: `mvn spring-boot:run`
3. Verificar os logs no console:
```
✅ Dados de teste criados:
   Admin:    admin@toyota.com / admin123
   Vendedor: vendedor@toyota.com / vendedor123
   Cliente:  cliente@gmail.com / cliente123
```
4. Testar login com cada usuário:
```bash
# Admin
curl -X POST http://localhost:8080/api/usuario/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@toyota.com","senha":"admin123"}'

# Vendedor
curl -X POST http://localhost:8080/api/usuario/login \
  -H "Content-Type: application/json" \
  -d '{"email":"vendedor@toyota.com","senha":"vendedor123"}'

# Cliente
curl -X POST http://localhost:8080/api/usuario/login \
  -H "Content-Type: application/json" \
  -d '{"email":"cliente@gmail.com","senha":"cliente123"}'
```

---
---

## 📋 Resumo dos Cards

| Card | Tipo | Prioridade | Tempo | Arquivos |
|------|------|-----------|-------|----------|
| BLOQ-01 | Bug | BLOQUEADOR | 5 min | 1 arquivo editado |
| BLOQ-02 | Feature | BLOQUEADOR | 15 min | 2 arquivos novos |
| IMP-01 | Doc | IMPORTANTE | 20 min | 1 arquivo novo + 2 editados |
| IMP-02 | Infra | IMPORTANTE | 5 min | 1 arquivo novo + 1 editado |
| IMP-03 | Infra | IMPORTANTE | 10 min | 1 arquivo novo |
| IMP-04 | Infra | IMPORTANTE | 15 min | 1 arquivo novo |

**Total Bloqueadores:** 20 minutos  
**Total Importantes:** 50 minutos  
**Total Geral:** 70 minutos

---

## 🚀 Ordem de Execução

```
1. BLOQ-01 → Corrigir CustomUserDetailsService (5min)
2. BLOQ-02 → Criar GlobalExceptionHandler (15min)
3. IMP-04  → Criar DataSeeder (15min)
4. IMP-02  → Criar HealthController (5min)
5. IMP-03  → Configurar CORS (10min)
6. IMP-01  → Configurar Swagger (20min)
```

**Após os 2 primeiros (20 min), a API já funciona para testes básicos.**
