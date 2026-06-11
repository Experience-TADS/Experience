/*
 * ============================================================
 *  SIMULADOR DE LINHA DE PRODUÇÃO — ESP32
 *  Base: exemplo.ino (LED GPIO 32, botão GPIO 34)
 *  Acréscimo: WiFi + MQTT + geração de dados dos carros
 *
 *  Dependências (Library Manager):
 *    - PubSubClient  (Nick O'Leary)
 *    - ArduinoJson   (Benoit Blanchon)
 * ============================================================
 */

#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

// ============================================================
//  PINOS — mantidos do exemplo.ino
// ============================================================

int led    = 32;
int button = 34;
bool state = false;

// ============================================================
//  CONFIGURAÇÕES WiFi / MQTT — ajuste conforme seu ambiente
// ============================================================

const char* WIFI_SSID      = "WIFI_ADM_CFP402";
const char* WIFI_PASSWORD  = "ac4ce0ss2";

const char* MQTT_BROKER    = "10.109.3.9";  // IP do host com o broker
const int   MQTT_PORT      = 1883;
const char* MQTT_TOPIC     = "senai/pii_toy";
const char* MQTT_CLIENT_ID = "esp32-linha-producao";

// ============================================================
//  PARÂMETROS DA LINHA
// ============================================================

const int QTD_CARROS  = 10;
const int NUM_ETAPAS  = 6;

const char* ETAPAS[NUM_ETAPAS] = {
  "MONTAGEM_ESTRUTURAL",
  "PINTURA",
  "INSTALACAO_MOTOR",
  "ACABAMENTO_INTERNO",
  "INSPECAO_FINAL",
  "LIBERACAO_TRANSPORTE"
};

const int TEMPOS_ETAPA[NUM_ETAPAS] = { 3, 5, 4, 6, 3, 2 };

// ============================================================
//  ESTADO DA LINHA
// ============================================================

int  linha[NUM_ETAPAS];
int  tempo_restante[NUM_ETAPAS];
int  carro_atual        = 1;
int  carros_finalizados = 0;
bool simulacao_ativa    = false;

unsigned long ultimo_tick = 0;
const unsigned long TICK_MS = 1000;

// ============================================================
//  OBJETOS WiFi / MQTT
// ============================================================

WiFiClient   wifiClient;
PubSubClient mqttClient(wifiClient);

// ============================================================
//  FUNÇÕES AUXILIARES
// ============================================================

void conectar_wifi() {
  Serial.print("[WiFi] Conectando a ");
  Serial.println(WIFI_SSID);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.print("[WiFi] IP: ");
  Serial.println(WiFi.localIP());
}

void conectar_mqtt() {
  while (!mqttClient.connected()) {
    Serial.print("[MQTT] Conectando...");
    if (mqttClient.connect(MQTT_CLIENT_ID)) {
      Serial.println(" OK");
    } else {
      Serial.print(" Falhou (rc=");
      Serial.print(mqttClient.state());
      Serial.println("). Tentando em 3s...");
      delay(3000);
    }
  }
}

void gerar_chassi(int n, char* buf, size_t buf_size) {
  snprintf(buf, buf_size, "CHASSI_%05d", n);
}

void publicar(const char* chassi, const char* etapa, const char* status) {
  if (!mqttClient.connected()) {
    conectar_mqtt();
  }

  StaticJsonDocument<256> doc;
  doc["chassi"]    = chassi;
  doc["etapa"]     = etapa;
  doc["status"]    = status;
  doc["timestamp"] = millis();

  char payload[256];
  serializeJson(doc, payload);

  mqttClient.publish(MQTT_TOPIC, payload);
  Serial.println(payload);
}

void resetar_linha() {
  for (int i = 0; i < NUM_ETAPAS; i++) {
    linha[i]          = -1;
    tempo_restante[i] = 0;
  }
  carro_atual        = 1;
  carros_finalizados = 0;
}

void tick_simulacao() {
  for (int i = NUM_ETAPAS - 1; i >= 0; i--) {
    if (linha[i] != -1) {
      tempo_restante[i]--;

      if (tempo_restante[i] <= 0) {
        char chassi[16];
        gerar_chassi(linha[i], chassi, sizeof(chassi));
        publicar(chassi, ETAPAS[i], "Finalizado");

        if (i == NUM_ETAPAS - 1) {
          carros_finalizados++;
          linha[i] = -1;
        } else {
          if (linha[i + 1] == -1) {
            linha[i + 1]          = linha[i];
            tempo_restante[i + 1] = TEMPOS_ETAPA[i + 1];
            gerar_chassi(linha[i + 1], chassi, sizeof(chassi));
            publicar(chassi, ETAPAS[i + 1], "Iniciado");
            linha[i] = -1;
          }
        }
      }
    }
  }

  if (carro_atual <= QTD_CARROS && linha[0] == -1) {
    linha[0]         = carro_atual;
    tempo_restante[0] = TEMPOS_ETAPA[0];
    char chassi[16];
    gerar_chassi(carro_atual, chassi, sizeof(chassi));
    publicar(chassi, ETAPAS[0], "Iniciado");
    carro_atual++;
  }

  if (carros_finalizados >= QTD_CARROS) {
    simulacao_ativa = false;
    Serial.println("[LINHA] Simulacao finalizada.");
    // LED apaga quando termina (igual ao exemplo: state false = LOW)
    state = false;
    digitalWrite(led, LOW);
  }
}

// ============================================================
//  SETUP
// ============================================================

void setup() {
  pinMode(button, INPUT);
  pinMode(led, OUTPUT);
  Serial.begin(9600);   // mantido do exemplo.ino

  conectar_wifi();
  mqttClient.setBufferSize(512);
  mqttClient.setServer(MQTT_BROKER, MQTT_PORT);
  conectar_mqtt();

  Serial.println("=== LINHA DE PRODUCAO ESP32 ===");
  Serial.println("Pressione o botao para iniciar/parar.");
}

// ============================================================
//  LOOP
// ============================================================

void loop() {

  // --- Lógica do botão (igual ao exemplo.ino) ---
  if (!digitalRead(button)) {
    while (!digitalRead(button));
    delay(50);  // debounce
    state = !state;

    if (state) {
      // Botão ligou: inicia simulação
      resetar_linha();
      simulacao_ativa = true;
      ultimo_tick     = millis();
      Serial.println("[LINHA] Iniciada.");
    } else {
      // Botão desligou: para simulação
      simulacao_ativa = false;
      Serial.println("[LINHA] Parada pelo botao.");
    }
  }

  // LED reflete o state (igual ao exemplo.ino)
  digitalWrite(led, state ? HIGH : LOW);

  // --- MQTT keepalive ---
  if (!mqttClient.connected()) {
    conectar_mqtt();
  }
  mqttClient.loop();

  // --- Tick da linha (1x por segundo, só se ativa) ---
  if (simulacao_ativa) {
    unsigned long agora = millis();
    if (agora - ultimo_tick >= TICK_MS) {
      ultimo_tick = agora;
      tick_simulacao();
    }
  }
}