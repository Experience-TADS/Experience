package com.senai.experience.controllers;

import com.senai.experience.DTO.request.LoginRequest;
import com.senai.experience.DTO.request.UsuarioRequest;
import com.senai.experience.DTO.response.LoginResponse;
import com.senai.experience.DTO.response.UsuarioResponse;
import com.senai.experience.entities.Usuario;
import com.senai.experience.mappers.UsuarioMapper;
import com.senai.experience.security.JwtUtil;
import com.senai.experience.services.UsuarioService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> getAllUsuarios() {
        return usuarioService.findAll()
                .stream()
                .map(UsuarioMapper::toResponse)
                .toList();
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
    public ResponseEntity<UsuarioResponse> createUsuario(@RequestBody UsuarioRequest dto) {
        Usuario salvo = usuarioService.save(UsuarioMapper.toEntity(dto));
        return ResponseEntity.status(201).body(UsuarioMapper.toResponse(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable Long id, @RequestBody UsuarioRequest dto) {
        Usuario u = UsuarioMapper.toEntity(dto);
        u.setId(id);
        Usuario atualizado = usuarioService.update(u);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UsuarioMapper.toResponse(atualizado));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.login(loginRequest.getEmail(), loginRequest.getSenha());
        if (usuario == null) {
            return ResponseEntity.status(401).body("Email ou senha inválidos.");
        }
        String token = JwtUtil.generateToken(usuario.getEmail());
        return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(), usuario.getRole()));
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
}
