package com.senai.experience.controllers;

import com.senai.experience.DTO.request.LoginRequest;
import com.senai.experience.DTO.request.UsuarioRequest;
import com.senai.experience.DTO.response.LoginResponse;
import com.senai.experience.DTO.response.UsuarioResponse;
import com.senai.experience.entities.Usuario;
import com.senai.experience.mappers.UsuarioMapper;
import com.senai.experience.security.JwtUtil;
import com.senai.experience.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários", description = "Gerenciamento de usuários: cadastro, autenticação, ativação e desativação")
@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados de forma paginada. Requer autenticação.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado — token ausente ou inválido", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public Page<UsuarioResponse> getAllUsuarios(Pageable pageable) {
        return usuarioService.findAll(pageable)
                .map(UsuarioMapper::toResponse);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico pelo seu identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getUsuarioById(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    @Operation(summary = "Cadastrar usuário", description = "Cria um novo usuário no sistema. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos no corpo da requisição", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(@RequestBody UsuarioRequest dto) {
        Usuario salvo = usuarioService.save(UsuarioMapper.toEntity(dto));
        return ResponseEntity.status(201).body(UsuarioMapper.toResponse(salvo));
    }

    @Operation(summary = "Remover usuário", description = "Remove um usuário pelo ID. Requer permissão de ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
            @RequestBody UsuarioRequest dto) {
        Usuario u = UsuarioMapper.toEntity(dto);
        u.setId(id);
        Usuario atualizado = usuarioService.update(u);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UsuarioMapper.toResponse(atualizado));
    }

    @Operation(
        summary = "Autenticar usuário",
        description = "Realiza login com e-mail e senha. Retorna um token JWT para uso nos demais endpoints."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso — token JWT retornado",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.login(loginRequest.getEmail(), loginRequest.getSenha());
        if (usuario == null) {
            return ResponseEntity.status(401).body("Email ou senha inválidos.");
        }
        String token = JwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRole() != null ? usuario.getRole().name() : "CLIENTE",
                usuario.getId());
        return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(), usuario.getRole()));
    }

    @Operation(
        summary = "Dados do usuário autenticado",
        description = "Retorna os dados do usuário proprietário do token JWT informado no header Authorization."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Token não fornecido", content = @Content),
        @ApiResponse(responseCode = "401", description = "Token inválido ou expirado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
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
        }        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    @Operation(summary = "Ativar usuário", description = "Ativa a conta de um usuário desativado. Exclusivo para ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário ativado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado — requer ADMIN", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> ativar(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id) {
        Usuario usuario = usuarioService.ativar(id);
        if (usuario == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    @Operation(summary = "Desativar usuário", description = "Desativa a conta de um usuário ativo. Exclusivo para ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário desativado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado — requer ADMIN", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> desativar(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id) {
        Usuario usuario = usuarioService.desativar(id);
        if (usuario == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }
}
