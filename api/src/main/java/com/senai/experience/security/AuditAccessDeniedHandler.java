package com.senai.experience.security;

import com.senai.experience.services.AuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Intercepta todo acesso negado (403), registra no audit.log e devolve 403.
 * Critério S-010: log de acessos negados com usuário e recurso desejado.
 */
@Component
public class AuditAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditService auditService;

    public AuditAccessDeniedHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        auditService.logAcessoNegado(
                resolverUsuario(),
                request.getMethod(),
                request.getRequestURI(),
                resolverIp(request)
        );

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
    }

    private String resolverUsuario() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "anonimo";
    }

    private String resolverIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
