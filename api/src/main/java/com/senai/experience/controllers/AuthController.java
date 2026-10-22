package com.senai.experience.controllers;

import com.senai.experience.DTO.request.RefreshRequest;
import com.senai.experience.DTO.response.RefreshResponse;
import com.senai.experience.entities.Usuario;
import com.senai.experience.security.JwtUtil;
import com.senai.experience.services.AuditService;
import com.senai.experience.services.RefreshTokenInvalidoException;
import com.senai.experience.services.RefreshTokenService;
import com.senai.experience.services.RefreshTokenService.ResultadoRotacao;
import com.senai.experience.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Renovação de tokens de acesso via refresh token com rotação")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final UsuarioService usuarioService;
    private final AuditService auditService;

    public AuthController(RefreshTokenService refreshTokenService,
                          UsuarioService usuarioService,
                          AuditService auditService) {
        this.refreshTokenService = refreshTokenService;
        this.usuarioService = usuarioService;
        this.auditService = auditService;
    }

    @Operation(
        summary = "Renovar access token",
        description = "Troca um refresh token válido por um novo par (access + refresh). "
                + "O refresh token apresentado é invalidado (rotação). Reutilizar um refresh "
                + "token revogado, expirado ou inexistente retorna 401."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Novo par de tokens emitido",
            content = @Content(schema = @Schema(implementation = RefreshResponse.class))),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido, revogado ou expirado",
            content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest req, HttpServletRequest request) {
        try {
            ResultadoRotacao r = refreshTokenService.rotacionar(req.getRefreshToken());
            Usuario u = usuarioService.findById(r.usuarioId());
            if (u == null) {
                auditService.logRefreshFalha(resolverIp(request));
                return ResponseEntity.status(401).body("Refresh token inválido.");
            }
            String access = JwtUtil.generateToken(
                    u.getEmail(),
                    u.getRole() != null ? u.getRole().name() : "CLIENTE",
                    u.getId());
            auditService.logRefreshSucesso(u.getEmail());
            return ResponseEntity.ok(new RefreshResponse(access, r.novoRefreshToken()));
        } catch (RefreshTokenInvalidoException e) {
            auditService.logRefreshFalha(resolverIp(request));
            return ResponseEntity.status(401).body("Refresh token inválido ou revogado.");
        }
    }

    private String resolverIp(HttpServletRequest request) {
        if (request == null) {
            return "desconhecido";
        }
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
