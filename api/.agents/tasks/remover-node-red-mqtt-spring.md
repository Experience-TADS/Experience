# Card: Remover Node-RED e integrar MQTT direto no Spring

**Status:** ✅ Concluído — novembro/2026

---

## Contexto

O fluxo IoT anterior dependia do **Node-RED** como intermediário:

```
ESP32 ──MQTT (senai/pii_toy)──> Mosquitto ──> Node-RED ──┬──HTTP POST──> API Spring
                                                          └──> InfluxDB (bucket linha_producao)
```

O objetivo desta tarefa foi **remover o Node-RED** e fazer o backend Spring assinar o broker
MQTT diretamente.

---

## Decisões de arquitetura

| Ponto | Decisão | Justificativa |
|---|---|---|
| Biblioteca MQTT | **Spring Integration MQTT** (Paho) | Integra nativamente ao ciclo de vida Spring; reconexão automática; sem necessidade de gerenciar threads manualmente |
| InfluxDB time-series | **Descontinuado** | O Node-RED era o único produtor. Grafana pode ser reconfigurado para PostgreSQL se necessário |
| Endpoint HTTP `/nodered/evento` | **Removido** | Sem Node-RED, o endpoint não tem mais consumidor externo; a lógica migrou para o listener MQTT |

---

## Arquivos modificados

### Backend (`api/`)

| Arquivo | Mudança |
|---|---|
| `pom.xml` | Adicionado `spring-integration-mqtt:6.3.5` |
| `src/main/resources/application.properties` | Tópico corrigido para `senai/pii_toy`; URL do broker via `MQTT_BROKER_URL` (padrão dev / docker) |
| `src/main/java/.../mqtt/MqttConfig.java` | **Novo** — configura factory Paho, canal e adaptador inbound |
| `src/main/java/.../mqtt/MqttEspPayload.java` | **Novo** — DTO do payload do ESP32 |
| `src/main/java/.../mqtt/MqttEspEventoListener.java` | **Novo** — handler MQTT que reutiliza `VeiculoService` |
| `src/main/java/.../services/VeiculoService.java` | `parseChassi` movido do controller para cá (público) |
| `src/main/java/.../controllers/VeiculoController.java` | Endpoint `/nodered/evento` e inner class `NodeRedEventoRequest` removidos |
| `src/main/java/.../security/SecurityConfig.java` | Regra `permitAll` do `/nodered/evento` removida |
| `src/test/.../unit/VeiculoServiceTest.java` | **Novo** — 15 testes unitários cobrindo `mapearEtapaParaStatus`, `parseChassi` e `atualizarStatus` |
| `src/test/.../restassured/VeiculoRestAssuredTest.java` | Teste do endpoint removido excluído; javadoc atualizado |

### Infraestrutura

| Arquivo | Mudança |
|---|---|
| `docker-compose.yml` | Serviço `nodered`, volume `nodered_data` removidos; `MQTT_BROKER_URL` e `depends_on: mqtt` adicionados à `api` |
| `docker-compose.aws.yml` | Idem |
| `scripts/start-aws.sh` | Menção ao Node-RED removida |

### Documentação / coleções

| Arquivo | Mudança |
|---|---|
| `README.md` | Referências ao Node-RED atualizadas para novo fluxo MQTT direto |
| `mqtt/esp32/README.md` | Topologia e troubleshooting atualizados |
| `api/Experience-API.postman_collection.json` | Item "Evento Node-RED" marcado como removido com instrução de substituição |
| `postman/IoT_Integration_Tests.postman_collection.json` | Descrição atualizada |
| `mqtt/nodered/archived/file.json` | Flow original arquivado (não excluído) |
| `mqtt/nodered/archived/README.md` | Explica o motivo do arquivamento |

---

## Fluxo novo

```
ESP32 / simulador ──MQTT (senai/pii_toy)──> Mosquitto ──> Spring Boot
                                                           MqttEspEventoListener
                                                                │
                                                       VeiculoService.parseChassi
                                                       VeiculoService.mapearEtapaParaStatus
                                                       VeiculoService.atualizarStatus
                                                                │
                                                           PostgreSQL
                                                        (Veiculo + StatusHistorico)
```

---

## Critérios de aceite — checagem

- [x] Container `experience-nodered` não existe em nenhum compose
- [x] Publicar em `senai/pii_toy` atualiza `StatusFabricacao` sem passar pelo Node-RED
- [x] Backend conecta via `tcp://mqtt:1883` no Docker e reconecta automaticamente (`automaticReconnect=true`)
- [x] Decisões sobre InfluxDB e endpoint HTTP documentadas e implementadas
- [x] `mvn test` cobre `mapearEtapaParaStatus`, `parseChassi` e `atualizarStatus`
- [x] Documentação e coleções Postman refletem o novo fluxo
