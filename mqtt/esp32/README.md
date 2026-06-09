# Simulador de Linha de Produção — ESP32

Migração do `simulador.py` para um ESP32 físico conectado via WiFi ao broker MQTT.  
A lógica de simulação é idêntica: 10 carros passam por 6 etapas sequenciais, publicando eventos JSON no tópico `senai/pii_toy`.

---

## Estrutura do projeto

```
esp32/
└── linha_producao/
    └── linha_producao.ino   ← sketch principal
```

---

## Hardware necessário

| Componente | Observação |
|---|---|
| ESP32 Dev Board | Qualquer variante com WiFi (WROOM, WROVER, etc.) |
| Cabo USB | Para gravar o firmware e monitorar o Serial |
| Rede WiFi 2.4 GHz | O ESP32 não suporta 5 GHz |

> O LED onboard (GPIO 2) acende enquanto a simulação está rodando e apaga ao finalizar.

---

## Dependências — Arduino IDE

Instale pelo **Library Manager** (`Sketch → Include Library → Manage Libraries`):

| Biblioteca | Autor | Versão testada |
|---|---|---|
| PubSubClient | Nick O'Leary | 2.8+ |
| ArduinoJson | Benoit Blanchon | 6.x |

Placa: **esp32 by Espressif Systems** (Board Manager) — versão 3.3.8+

---

## Configuração antes de gravar

Abra `linha_producao.ino` e edite as constantes no topo:

```cpp
const char* WIFI_SSID     = "SEU_WIFI";       // nome da sua rede
const char* WIFI_PASSWORD = "SUA_SENHA";       // senha da rede

const char* MQTT_BROKER   = "192.168.1.100";   // IP do host com o broker Mosquitto
const int   MQTT_PORT     = 1883;
```

> **Como descobrir o IP do broker:**  
> No host que roda o `docker-compose`, execute `ipconfig` (Windows) ou `ip a` (Linux).  
> Use o IP da interface de rede local (ex: `192.168.1.x`), **não** `localhost` nem `127.0.0.1`.

---

## Como gravar no ESP32

1. Abra o Arduino IDE 2.x
2. Selecione a placa: `Tools → Board → ESP32 Dev Board`
3. Selecione a porta COM correta em `Tools → Port`
4. Clique em **Upload** (→)
5. Abra o **Serial Monitor** (`115200 baud`) para acompanhar os logs

---

## Lógica da simulação

A lógica é uma tradução direta do `simulador.py`:

```
Linha:  [ etapa0 | etapa1 | etapa2 | etapa3 | etapa4 | etapa5 ]
         MONTAGEM  PINTURA  MOTOR   ACABAM.  INSPEC.  TRANSP.
Tempos:     3s       5s      4s       6s       3s       2s
```

### Fluxo por tick (1 tick = 1 segundo)

1. Percorre as etapas **de trás para frente**
2. Decrementa o tempo restante de cada etapa ocupada
3. Quando o tempo chega a zero:
   - Publica `"Finalizado"` para aquela etapa
   - Se houver etapa seguinte livre → move o carro e publica `"Iniciado"`
   - Se for a última etapa → incrementa `carros_finalizados`
4. Se a etapa 0 estiver livre e ainda houver carros → insere novo carro e publica `"Iniciado"`
5. Quando `carros_finalizados == 10` → simulação encerra

---

## Formato da mensagem MQTT

Idêntico ao `simulador.py`, compatível com o Node-RED e InfluxDB já configurados:

```json
{
  "chassi":    "CHASSI_00001",
  "etapa":     "MONTAGEM_ESTRUTURAL",
  "status":    "Iniciado",
  "timestamp": 4521
}
```

> `timestamp` é o tempo em milissegundos desde o boot do ESP32.  
> Se precisar de data/hora real, integre com NTP usando a biblioteca `ESP32Time`.

---

## Diferenças em relação ao simulador.py

| Aspecto | simulador.py | ESP32 |
|---|---|---|
| Conexão MQTT | `paho-mqtt` via Docker | `PubSubClient` via WiFi |
| Timestamp | `datetime` (data/hora real) | `millis()` (ms desde boot) |
| Broker address | `"mqtt"` (nome do serviço Docker) | IP local do host |
| Execução | Loop bloqueante com `time.sleep(1)` | Loop não-bloqueante com `millis()` |
| Reconexão | Não implementada | Reconecta WiFi e MQTT automaticamente |

---

## Topologia do sistema

```
┌─────────────┐   WiFi/MQTT    ┌───────────────┐   MQTT   ┌──────────┐
│   ESP32     │ ─────────────► │   Mosquitto   │ ◄──────► │ Node-RED │
│ (simulador) │                │  (broker)     │          └──────────┘
└─────────────┘                └───────────────┘               │
                                                               ▼
                                                         ┌──────────┐
                                                         │ InfluxDB │
                                                         └──────────┘
                                                               │
                                                               ▼
                                                         ┌──────────┐
                                                         │ Grafana  │
                                                         └──────────┘
```

---

## Troubleshooting

| Sintoma | Causa provável | Solução |
|---|---|---|
| Serial trava em `Conectando...` | WiFi errado ou sinal fraco | Verifique SSID/senha; aproxime o ESP32 do roteador |
| MQTT `rc=-2` | IP do broker errado | Confirme o IP com `ipconfig` no host |
| MQTT `rc=-4` | Broker não acessível | Verifique se o container `mqtt` está rodando (`docker ps`) |
| Nenhuma mensagem no Node-RED | Tópico diferente | Confirme que o tópico é `senai/pii_toy` nos dois lados |
| Upload falha | Driver USB não instalado | Instale o driver CP210x ou CH340 conforme o chip do seu ESP32 |
