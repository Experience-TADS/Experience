package com.senai.experience.controllers;

import com.senai.experience.DTO.LoginRequest;
import com.senai.experience.entities.Usuario;
import com.senai.experience.security.JwtUtil;
import com.senai.experience.services.UsuarioService;

import java.util.List;
import java.util.Map;

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
    public List<Usuario> getAllUsuarios() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public Usuario getUsuarioById(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    @PostMapping
    public Usuario createUsuario(@RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }

    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
    }

    @PutMapping("/{id}")
    public Usuario updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        usuario.setId(id);
        return usuarioService.update(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.login(loginRequest.getEmail(), loginRequest.getSenha());
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("erro", "Email ou senha inválidos."));
        }
        String token = JwtUtil.generateToken(usuario.getEmail());
        return ResponseEntity.ok(Map.of("token", token));
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
        return ResponseEntity.ok(usuario);
    }
}
