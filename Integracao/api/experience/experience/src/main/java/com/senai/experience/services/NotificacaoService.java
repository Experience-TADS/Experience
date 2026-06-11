package com.senai.experience.services;

import com.senai.experience.entities.EtapaTemplate;
import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.Usuario;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.repositories.EtapaTemplateRepository;
import com.senai.experience.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final EtapaTemplateRepository templateRepo;
    private final PedidoRepository pedidoRepo;
    private final RestTemplate restTemplate;

    public void notificarMudancaEtapa(Veiculo veiculo, StatusFabricacao novoStatus) {

        // 1. Busca o template da etapa
        EtapaTemplate template = templateRepo.findByStatus(novoStatus).orElse(null);
        if (template == null) {
            log.warn("Template não encontrado para status: {}", novoStatus);
            return;
        }

        // 2. Busca o cliente dono do veículo via Pedido → ItemPedido
        Usuario cliente = pedidoRepo.findClienteByVeiculoId(veiculo.getId()).orElse(null);
        if (cliente == null) {
            log.warn("Cliente não encontrado para o veículo id: {}", veiculo.getId());
            return;
        }

        if (cliente.getFcmToken() == null || cliente.getFcmToken().isBlank()) {
            log.warn("FCM token não cadastrado para o cliente: {}", cliente.getEmail());
            return;
        }

        // 3. Personaliza a mensagem substituindo as variáveis
        String mensagem = template.getMensagem()
            .replace("{nome}", extrairPrimeiroNome(cliente.getNome()))
            .replace("{modelo}", veiculo.getProduto().getModelo())
            .replace("{cor}", veiculo.getProduto().getCor());

        // 4. Envia via Expo Push Service
        enviarPush(
            cliente.getFcmToken(),
            template.getTitulo(),
            mensagem,
            template.getFotoUrl(),
            novoStatus.name()
        );
    }

    private void enviarPush(String expoToken, String titulo, String corpo,
                             String imagemUrl, String etapa) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Accept-Encoding", "gzip, deflate");

            Map<String, Object> payload = Map.of(
                "to", expoToken,
                "title", titulo,
                "body", corpo,
                "data", Map.of(
                    "etapa", etapa,
                    "imagemUrl", imagemUrl != null ? imagemUrl : ""
                )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            String response = restTemplate.postForObject(EXPO_PUSH_URL, request, String.class);
            log.info("Notificação Expo enviada. Resposta: {}", response);

        } catch (Exception e) {
            // Falha na notificação não deve bloquear o fluxo principal
            log.error("Erro ao enviar notificação Expo: {}", e.getMessage());
        }
    }

    private String extrairPrimeiroNome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isBlank()) return "";
        return nomeCompleto.split(" ")[0];
    }
}
