package com.senai.experience.controllers;

import com.senai.experience.DTO.response.DashboardResponse;
import com.senai.experience.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint agregado para o app mobile.
 *
 * <p>{@code GET /api/dashboard} retorna, em uma única requisição:</p>
 * <ul>
 *   <li>Dados do usuário autenticado (nome, email, role)</li>
 *   <li>Lista de pedidos do cliente com status de fabricação atual</li>
 *   <li>Timeline de etapas de fabricação por pedido (histórico)</li>
 *   <li>Percentual de progresso por pedido (0–100%)</li>
 * </ul>
 *
 * <p>Isso evita que o app precise fazer múltiplas chamadas sequenciais
 * para montar a tela principal, melhorando a performance em redes móveis.</p>
 *
 * <h3>Exemplo de uso no app (React Native / Expo):</h3>
 * <pre>{@code
 * const response = await api.get('/api/dashboard');
 * const { usuario, pedidos, totalPedidos } = response.data;
 * }</pre>
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Retorna o dashboard completo do cliente autenticado.
     *
     * <p>Requer token JWT no header {@code Authorization: Bearer {token}}.
     * O email do usuário é extraído automaticamente do token pelo Spring Security.</p>
     *
     * @param auth contexto de autenticação injetado pelo Spring Security
     * @return {@link DashboardResponse} com dados agregados do cliente
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication auth) {
        DashboardResponse response = dashboardService.getDashboard(auth.getName());
        return ResponseEntity.ok(response);
    }
}
