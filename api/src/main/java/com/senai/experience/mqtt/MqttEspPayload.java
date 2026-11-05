package com.senai.experience.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Payload publicado pelo ESP32 / simulador no tópico {@code senai/pii_toy}.
 *
 * <pre>
 * {
 *   "chassi":    "CHASSI_00001",
 *   "etapa":     "PINTURA",
 *   "status":    "Iniciado" | "Finalizado",
 *   "timestamp": 12345
 * }
 * </pre>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MqttEspPayload {

    private String chassi;
    private String etapa;
    private String status;
    private Long timestamp;
}
