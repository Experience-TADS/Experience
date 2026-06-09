# Roteiro de Integração — ESP32 + MQTT + Node-RED + Backend

> **Estratégia:** O ESP32 publica no tópico MQTT. O Node-RED assina esse tópico,
> transforma os dados e faz as requisições HTTP para o backend Spring Boot.
> O backend não precisa de nenhum cliente MQTT — toda a ponte é feita pelo Node-RED.

---

## Visão Geral do Fluxo

```
ESP32
  └─ publica MQTT → tópico: senai/pii_toy
        │
        ▼
   Broker MQTT (Mosquitto — 10.109.3.142:1883)
        │
        ▼
     Node-RED
       ├─ assina senai/pii_toy
       ├─ mapeia etapa ESP32 → StatusFabricacao do backend
       ├─ busca veículo pelo chassi (GET /api/veiculo/chassi/{chassi})
       └─ atualiza status (POST /api/veiculo/{id}/status)
        │
        ▼
  Backend Spring Boot (localhost:8080)
```

---

## Mapeamento de Etapas

O ESP32 usa nomes de etapas diferentes do enum `StatusFabricacao` do backend.
O Node-RED faz essa tradução:

| Etapa ESP32 (`etapa`)      | StatusFabricacao (backend) |
|----------------------------|---------------------------|
| `MONTAGEM_ESTRUTURAL`      | `EM_FABRICACAO`           |
| `PINTURA`                  | `PINTURA`                 |
| `INSTALACAO_MOTOR`         | `EM_FABRICACAO`           |
| `ACABAMENTO_INTERNO`       | `EM_FABRICACAO`           |
| `INSPECAO_FINAL`           | `CONTROLE_QUALIDADE`      |
| `LIBERACAO_TRANSPORTE`     | `CONCLUIDO`               |

> **Atenção:** O ESP32 publica `status: "Iniciado"` e `status: "Finalizado"` para cada etapa.
> O Node-RED deve reagir **apenas ao `"Finalizado"`** para avançar o status no backend,
> evitando transições duplicadas.
>
> **Nota:** O backend agora ignora silenciosamente chamadas onde o novo status é igual
> ao atual (ex: `MONTAGEM_ESTRUTURAL`, `INSTALACAO_MOTOR` e `ACABAMENTO_INTERNO` mapeiam
> para `EM_FABRICACAO` — apenas a primeira chamada altera o banco, as demais são ignoradas
> sem erro).

---

## Payload MQTT do ESP32

```json
{
  "chassi": "CHASSI_00001",
  "etapa": "MONTAGEM_ESTRUTURAL",
  "status": "Finalizado",
  "timestamp": 12345
}
```

---

## Passo 1 — Endpoint de status já liberado ✅

O endpoint `POST /api/veiculo/{id}/status` já está configurado como `permitAll()`
no `SecurityConfig.java`. Nenhuma alteração necessária.

```java
// SecurityConfig.java — já configurado assim:
.requestMatchers(HttpMethod.POST, "/api/veiculo/*/status").permitAll()
```

> Se quiser mais segurança em produção, crie um usuário com `role: IOT` via
> `POST /api/usuario`, faça login, e configure o token como header fixo no Node-RED.

---

## Passo 2 — Verificar endpoint de busca por chassi

O Node-RED precisa descobrir o `id` do veículo pelo chassi antes de atualizar o status.

**Endpoint existente:** `GET /api/veiculo/chassi/{chassi}`

O chassi do ESP32 vem como string `"CHASSI_00001"`. O backend tem `chassi` como `int`.

**Solução no Node-RED** — extrair só o número antes de chamar a API:
```javascript
msg.numeroChassi = parseInt(msg.payload.chassi.replace("CHASSI_", ""));
```

---

## Passo 3 — Fluxo Node-RED

### Nodes necessários

```
[mqtt in] → [json] → [function: filtrar] → [function: mapear] →
  → [http request: buscar veículo] → [function: extrair id] →
  → [http request: atualizar status] → [debug]
```

### Node 1 — MQTT In
- **Server:** `10.109.3.142:1883`
- **Topic:** `senai/pii_toy`
- **Output:** parsed JSON

### Node 2 — Function: Filtrar (só "Finalizado")
```javascript
// Só processa quando a etapa foi finalizada
if (msg.payload.status !== "Finalizado") {
    return null; // descarta
}
return msg;
```

### Node 3 — Function: Mapear etapa → StatusFabricacao
```javascript
const mapa = {
    "MONTAGEM_ESTRUTURAL":  "EM_FABRICACAO",
    "PINTURA":              "PINTURA",
    "INSTALACAO_MOTOR":     "EM_FABRICACAO",
    "ACABAMENTO_INTERNO":   "EM_FABRICACAO",
    "INSPECAO_FINAL":       "CONTROLE_QUALIDADE",
    "LIBERACAO_TRANSPORTE": "CONCLUIDO"
};

const etapa = msg.payload.etapa;
const statusBackend = mapa[etapa];

if (!statusBackend) {
    node.warn("Etapa desconhecida: " + etapa);
    return null;
}

// Extrai número do chassi: "CHASSI_00001" → 1
const numeroChassi = parseInt(msg.payload.chassi.replace("CHASSI_", ""));

msg.statusBackend  = statusBackend;
msg.numeroChassi   = numeroChassi;
msg.chassiOriginal = msg.payload.chassi;
msg.url = "http://localhost:8080/api/veiculo/chassi/" + numeroChassi;

return msg;
```

### Node 4 — HTTP Request: Buscar veículo pelo chassi
- **Method:** GET
- **URL:** usar `msg.url`

### Node 5 — Function: Extrair ID do veículo
```javascript
const veiculo = JSON.parse(msg.payload);

if (!veiculo || !veiculo.id) {
    node.warn("Veículo não encontrado para chassi: " + msg.chassiOriginal);
    return null;
}

msg.veiculoId = veiculo.id;
msg.url = "http://localhost:8080/api/veiculo/" + msg.veiculoId + "/status";
msg.payload = msg.statusBackend; // body da requisição
msg.headers = {
    "Content-Type": "application/json"
    // Se usar token: "Authorization": "Bearer SEU_TOKEN_AQUI"
};

return msg;
```

### Node 6 — HTTP Request: Atualizar status
- **Method:** POST
- **URL:** usar `msg.url`
- **Body:** `msg.payload` (string com o enum, ex: `"EM_FABRICACAO"`)

> **Atenção:** O backend espera o body como string JSON pura, não objeto.
> O `msg.payload` deve ser `"EM_FABRICACAO"` (com aspas), não `EM_FABRICACAO`.

> **Comportamento com status duplicado:** Se o veículo já estiver em `EM_FABRICACAO`
> e o Node-RED enviar `EM_FABRICACAO` novamente (ex: etapas `INSTALACAO_MOTOR` ou
> `ACABAMENTO_INTERNO`), o backend retorna 200 com o último histórico — sem erro.
> Não é necessário nenhuma lógica extra no Node-RED para esse caso.

---

## Passo 4 — Aceitar string no body do status

O `StatusHistoricoController` recebe `@RequestBody StatusFabricacao novoStatus`.
O Spring deserializa uma string JSON `"EM_FABRICACAO"` direto para o enum — **não precisa alterar**.

Só garantir que o Node-RED mande o Content-Type correto:
```
Content-Type: application/json
Body: "EM_FABRICACAO"
```

---

## Passo 5 — Transições válidas (regra do backend)

O `StatusHistoricoService` valida as transições. O veículo começa em `AGUARDANDO`
(definido no POST de criação). O fluxo esperado é:

```
AGUARDANDO → EM_FABRICACAO → PINTURA → CONTROLE_QUALIDADE → CONCLUIDO → ENTREGUE
```

**Comportamento com status repetido (já implementado):**
O ESP32 manda `EM_FABRICACAO` para 3 etapas diferentes
(`MONTAGEM_ESTRUTURAL`, `INSTALACAO_MOTOR`, `ACABAMENTO_INTERNO`).
O `StatusHistoricoService` agora detecta que o status já é o mesmo e retorna
o último registro do histórico sem lançar exceção — o fluxo continua normalmente.

---

## Passo 6 — Criar veículo antes de iniciar a simulação

O ESP32 começa a publicar imediatamente ao pressionar o botão.
Os veículos precisam existir no banco **antes** da simulação.

Criar os 10 veículos via `POST /api/veiculo` com status `AGUARDANDO`.
O campo `idPedido` é opcional — use para vincular o veículo a um pedido existente:

```json
{
  "idProduto": 1,
  "chassi": 1,
  "statusVeiculo": "AGUARDANDO",
  "idPedido": 42
}
```

Sem pedido vinculado:
```json
{
  "idProduto": 1,
  "chassi": 1,
  "statusVeiculo": "AGUARDANDO"
}
```

Repetir para chassi 1 até 10.

---

## Passo 7 — tb_etapa_template (seed automático)

A tabela `tb_etapa_template` é populada automaticamente na inicialização do backend
pelo `EtapaTemplateSeeder`. Os 6 status de fabricação são inseridos apenas se ainda
não existirem (idempotente — seguro reiniciar o servidor).

| Status               | Título                    |
|----------------------|---------------------------|
| `AGUARDANDO`         | Aguardando Fabricação     |
| `EM_FABRICACAO`      | Em Fabricação             |
| `PINTURA`            | Pintura                   |
| `CONTROLE_QUALIDADE` | Controle de Qualidade     |
| `CONCLUIDO`          | Fabricação Concluída      |
| `ENTREGUE`           | Entregue                  |

> `CANCELADO` não possui template pois é um estado de exceção, não de progresso.

---

## Resumo das Alterações na API

| # | Arquivo | O que foi feito |
|---|---------|-----------------|
| 1 | `SecurityConfig.java` | `POST /api/veiculo/*/status` já está `permitAll()` ✅ |
| 2 | `Veiculo.java` | Campo `idPedido` (`Long`, nullable) adicionado à entidade |
| 3 | `VeiculoRequest.java` | Campo `idPedido` opcional adicionado ao DTO de entrada |
| 4 | `VeiculoResponse.java` | Campo `idPedido` adicionado ao DTO de saída |
| 5 | `VeiculoMapper.java` | Mapeamento de `idPedido` em `toEntity` e `toResponse` |
| 6 | `StatusHistoricoService.java` | Ignora chamada quando novo status == status atual (sem exceção) |
| 7 | `EtapaTemplateSeeder.java` | Seed automático dos 6 status na inicialização |

---

## Bug no .ino

Na última linha do `loop()` há um typo:
```cpp
// ERRADO
Atick_simulacao();

// CORRETO
tick_simulacao();
```

---

## Ordem de Execução para Testar

```
1. Subir Mosquitto broker (10.109.3.142:1883)
2. Subir backend Spring Boot (mvn spring-boot:run)
   └─ EtapaTemplateSeeder popula tb_etapa_template automaticamente
3. Criar produto via POST /api/produto
4. Criar 10 veículos via POST /api/veiculo (chassi 1-10, status AGUARDANDO)
5. Subir Node-RED e importar o flow
6. Ligar ESP32 e pressionar o botão
7. Monitorar o debug do Node-RED e o banco de dados
```
