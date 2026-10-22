package com.senai.experience.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Entry point de autenticação que devolve 401 para requisições sem credenciais.
 *
 * <p>A convenção padrão desta API é responder 403 para requisições anônimas (não há
 * entry point global configurado, então {@code .anyRequest().authenticated()} resulta
 * em 403 para o anônimo). O card de segurança do endpoint de status exige explicitamente
 * 401 quando a requisição não traz token.</p>
 *
 * <p>Por isso este entry point NÃO é registrado globalmente. Ele é associado apenas ao
 * endpoint {@code POST /api/veiculo/*&#47;status} via
 * {@code exceptionHandling().defaultAuthenticationEntryPointFor(entryPoint, matcher)} no
 * {@link SecurityConfig}, preservando o 403 padrão em todas as demais rotas.</p>
 */
@Component
public class StatusUnauthorizedEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Autenticação necessária");
    }
}
