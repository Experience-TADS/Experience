# Flow Node-RED Arquivado

Este flow (`file.json`) era responsável por:

1. Assinar o tópico MQTT `senai/pii_toy` (publicado pelo ESP32 / simulador).
2. Fazer POST para `http://api:8080/api/veiculo/nodered/evento` com o payload bruto.
3. Gravar métricas no InfluxDB (bucket `linha_producao`, org `senai`).

## Por que foi removido?

A partir da refatoração de **novembro/2026**, o backend Spring assina o broker MQTT
diretamente via **Spring Integration MQTT** (`MqttEspEventoListener`).

- O intermediário Node-RED foi eliminado — o fluxo agora é:
  `ESP32 → Mosquitto → Spring Boot`
- O endpoint HTTP `POST /api/veiculo/nodered/evento` foi removido.
- A escrita no InfluxDB foi **descontinuada** junto com o Node-RED.
  O Grafana pode ser reconfigurado para usar dados do PostgreSQL se necessário.

## Arquivos relacionados removidos / alterados

| Arquivo | Mudança |
|---|---|
| `docker-compose.yml` | Serviço `nodered` e volume `nodered_data` removidos |
| `docker-compose.aws.yml` | Idem + `MQTT_BROKER_URL` adicionado à `api` |
| `scripts/start-aws.sh` | Menção ao Node-RED removida |
| `api/pom.xml` | Adicionado `spring-integration-mqtt` |
| `api/.../mqtt/MqttConfig.java` | Novo — configuração do consumidor MQTT |
| `api/.../mqtt/MqttEspEventoListener.java` | Novo — handler de mensagens |
| `api/.../controllers/VeiculoController.java` | Endpoint `/nodered/evento` removido |
| `api/.../security/SecurityConfig.java` | Regra do endpoint removida |
