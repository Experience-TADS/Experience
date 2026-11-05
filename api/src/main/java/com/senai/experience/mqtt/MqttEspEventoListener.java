package com.senai.experience.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.services.VeiculoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * Consome mensagens MQTT do tópico {@code senai/pii_toy} e aplica a lógica de
 * atualização de status de fabricação já implementada em {@link VeiculoService}.
 *
 * <p>Comportamento por evento:</p>
 * <ul>
 *   <li>Payload inválido / chassi desconhecido → loga aviso e descarta (sem retry).</li>
 *   <li>Etapa/status sem mapeamento → ignora silenciosamente (mesmo comportamento do endpoint HTTP removido).</li>
 *   <li>Veículo não encontrado no banco → loga aviso e descarta.</li>
 *   <li>Exceção inesperada → loga erro sem propagar para não derrubar a sessão MQTT.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttEspEventoListener implements MessageHandler {

    private final VeiculoService veiculoService;
    private final ObjectMapper objectMapper;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String rawPayload = null;
        try {
            rawPayload = message.getPayload().toString();
            log.debug("MQTT recebido: {}", rawPayload);

            MqttEspPayload payload = objectMapper.readValue(rawPayload, MqttEspPayload.class);

            StatusFabricacao novoStatus =
                    veiculoService.mapearEtapaParaStatus(payload.getEtapa(), payload.getStatus());

            if (novoStatus == null) {
                log.debug("Evento MQTT ignorado — etapa='{}' status='{}' sem mapeamento relevante.",
                        payload.getEtapa(), payload.getStatus());
                return;
            }

            int chassiNum = veiculoService.parseChassi(payload.getChassi());
            if (chassiNum <= 0) {
                log.warn("MQTT: chassi inválido '{}', evento descartado.", payload.getChassi());
                return;
            }

            Veiculo atualizado = veiculoService.atualizarStatus(chassiNum, novoStatus);
            if (atualizado == null) {
                log.warn("MQTT: veículo com chassi {} não encontrado, evento descartado.", chassiNum);
                return;
            }

            log.info("MQTT: veículo chassi={} atualizado para status={}.",
                    chassiNum, novoStatus);

        } catch (Exception e) {
            log.error("MQTT: erro ao processar mensagem '{}': {}", rawPayload, e.getMessage(), e);
            // Não propaga a exceção para não encerrar a sessão MQTT
        }
    }
}
