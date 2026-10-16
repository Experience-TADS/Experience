package com.senai.experience.controllers;

import com.senai.experience.DTO.request.LoginRequest;
import com.senai.experience.DTO.request.UsuarioRequest;
import com.senai.experience.DTO.response.LoginResponse;
import com.senai.experience.DTO.response.UsuarioResponse;
import com.senai.experience.entities.Usuario;
import com.senai.experience.mappers.UsuarioMapper;
import com.senai.experience.security.JwtUtil;
import com.senai.experience.security.TokenBlacklist;
import com.senai.experience.services.AuditService;
import com.senai.experience.services.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TokenBlacklist tokenBlacklist;
    private final AuditService auditService;

    public UsuarioController(UsuarioService usuarioService,
                             TokenBlacklist tokenBlacklist,
                             AuditService auditService) {
        this.usuarioService = usuarioService;
        this.tokenBlacklist = tokenBlacklist;
        this.auditService = auditService;
    }

    @GetMapping
    public Page<UsuarioResponse> getAllUsuarios(Pageable pageable) {
        return usuarioService.findAll(pageable)
                .map(UsuarioMapper::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody UsuarioRequest dto) {
        Usuario salvo = usuarioService.save(UsuarioMapper.toEntity(dto));
        return ResponseEntity.status(201).body(UsuarioMapper.toResponse(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequest dto) {
        Usuario u = UsuarioMapper.toEntity(dto);
        u.setId(id);
        Usuario atualizado = usuarioService.update(u);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UsuarioMapper.toResponse(atualizado));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                   HttpServletRequest request) {
        String ip = resolverIp(request);
        Usuario usuario = usuarioService.login(loginRequest.getEmail(), loginRequest.getSenha());
        if (usuario == null) {
            // S-010: log de falha de login com IP
            auditService.logLoginFalha(loginRequest.getEmail(), ip);
            return ResponseEntity.status(401).body("Email ou senha inválidos.");
        }
        String token = JwtUtil.generateToken(usuario.getEmail(),
                usuario.getRole() != null ? usuario.getRole().name() : "CLIENTE");
        // S-010: log de login com sucesso com IP
        auditService.logLoginSucesso(usuario.getEmail(), ip);
        return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(), usuario.getRole()));
    }

    /**
     * S-012: Invalida o token atual adicionando-o à blacklist.
     * Fluxo testável: login → POST /api/usuario/logout (Bearer token) → mesmo token retorna 401.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = JwtUtil.validateToken(token) ? JwtUtil.extractUsername(token) : "desconhecido";
            tokenBlacklist.invalidate(token);
            // S-010: log de logout
            auditService.logLogout(email);
        }
        return ResponseEntity.noContent().build();
    }

    /** Extrai o IP real considerando proxies (X-Forwarded-For). */
    private String resolverIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token não fornecido.");
        }
        String token = authHeader.substring(7);
        if (!JwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido ou expirado.");
        }
        String email = JwtUtil.extractUsername(token);
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuário não encontrado.");
        }
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> ativar(@PathVariable Long id) {
        Usuario usuario = usuarioService.ativar(id);
        if (usuario == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> desativar(@PathVariable Long id) {
        Usuario usuario = usuarioService.desativar(id);
        if (usuario == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }
}
