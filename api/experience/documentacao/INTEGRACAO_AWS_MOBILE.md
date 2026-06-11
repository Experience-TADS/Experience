# Integração AWS + Desenvolvimento Mobile

> Implementação dos requisitos: Consumo de APIs / Desenvolvimento Mobile / Integração com Backend  
> Data: Junho 2026

---

## Arquitetura do Fluxo IoT

O Node-RED fica na AWS e faz a ponte entre o MQTT broker e a API. A API só recebe requisições HTTP — sem dependência de MQTT direto.

```
ESP32
  └─ publica MQTT → Broker Mosquitto (AWS EC2)
                          │
                          ▼
                       Node-RED (AWS EC2)
                         ├─ assina tópico MQTT
                         ├─ transforma o payload
                         └─ POST /api/veiculo/nodered/evento → API Spring Boot (AWS EC2)
                                                                        │
                                                                        ▼
                                                                   PostgreSQL (AWS RDS)
                                                                        │
                                                                        ▼
                                                               NotificacaoService → Expo Push → App
```

---

## O que foi implementado

### 1. Endpoint Node-RED → API (já existia, mantido)

`POST /api/veiculo/nodered/evento` — endpoint público que recebe o payload do Node-RED.

O Node-RED chama esse endpoint com:
```json
{
  "chassi":    "CHASSI_00001",
  "etapa":     "PINTURA",
  "status":    "Iniciado",
  "timestamp": 1234567890
}
```

A API mapeia etapa → `StatusFabricacao`, atualiza o banco e dispara o push notification Expo para o app mobile automaticamente.

**Configuração do Node-RED para apontar para a AWS:**  
No flow do Node-RED, altere a URL do node HTTP Request de `http://localhost:8080` para o IP/domínio da EC2:
```
http://<ip-publico-ec2>:8080/api/veiculo/nodered/evento
```

---

### 2. Endpoint de Dashboard para o App Mobile

`GET /api/dashboard` — retorna tudo que o app precisa em uma única requisição.

**Requer:** `Authorization: Bearer {token}`

**Resposta:**
```json
{
  "usuario": {
    "id": 1,
    "nome": "Maria Cliente",
    "email": "cliente@gmail.com",
    "role": "ROLE_CLIENTE"
  },
  "totalPedidos": 1,
  "pedidos": [
    {
      "idPedido": 42,
      "dataPedido": "2026-04-16T10:00:00",
      "valorTotal": 150000.00,
      "modeloVeiculo": "Toyota Corolla",
      "corVeiculo": "Preto",
      "statusAtual": "PINTURA",
      "statusDescricao": "Pintura em andamento",
      "progressoPercent": 29,
      "dataUltimaAtualizacao": "2026-06-09T14:30:00",
      "historico": [
        { "status": "AGUARDANDO",          "concluida": true,  "dataAlteracao": "2026-04-16T10:00:00" },
        { "status": "MONTAGEM_ESTRUTURAL", "concluida": true,  "dataAlteracao": "2026-06-08T09:15:00" },
        { "status": "PINTURA",             "concluida": false, "dataAlteracao": "2026-06-09T14:30:00" }
      ]
    }
  ]
}
```

---

### 3. Suporte a Variáveis de Ambiente (AWS)

O `application.properties` usa o padrão `${VARIAVEL:valor_local}` em todas as configurações sensíveis. Em produção na EC2, você só define as variáveis de ambiente — sem alterar código.

| Variável | Usado para |
|---|---|
| `SPRING_DATASOURCE_URL` | Endereço do RDS PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave de assinatura JWT |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | Credenciais AWS SES |
| `CORS_ALLOWED_ORIGINS` | Domínio do Amplify / IP do app |

---

### 4. CORS dinâmico

O `SecurityConfig` lê as origens permitidas de `cors.allowed.origins`. Em produção, defina:
```bash
CORS_ALLOWED_ORIGINS=https://app.experience.com,http://<ip-nodered>:1880
```

---

## Resumo dos Arquivos

| Arquivo | Ação | Propósito |
|---------|------|-----------|
| `DTO/response/DashboardResponse.java` | Criado | DTO do endpoint dashboard |
| `services/DashboardService.java` | Criado | Agrega dados para o app |
| `controllers/DashboardController.java` | Criado | `GET /api/dashboard` |
| `config/AppConfig.java` | Atualizado | Bean `ObjectMapper` com suporte a datas |
| `repositories/VeiculoRepository.java` | Atualizado | `findByIdPedido` |
| `security/SecurityConfig.java` | Atualizado | CORS dinâmico + rota dashboard |
| `application.properties` | Atualizado | Variáveis de ambiente para AWS |
| `Dockerfile` | Atualizado | Otimizado para AWS EC2 |
| `.env.example` | Criado | Template de configuração |
| `.github/workflows/deploy-aws.yml` | Criado | Pipeline CI/CD GitHub Actions → AWS |

---

## Como subir na AWS (EC2 + Docker)

### 1. Cria a EC2 (Amazon Linux 2 / Ubuntu, t3.small)
Abre as portas no Security Group: `8080` (API), `5432` (Postgres interno), `1880` (Node-RED).

### 2. Instala Docker na EC2
```bash
sudo yum update -y && sudo yum install docker -y
sudo service docker start
sudo usermod -aG docker ec2-user
```

### 3. Copia os arquivos e sobe
```bash
# Cria o .env com os valores reais baseado no .env.example
cp .env.example .env
nano .env  # preenche as variáveis

# Sobe Postgres + API
docker-compose up -d
```

### 4. Configura o Node-RED para apontar para a EC2
No flow do Node-RED, troca a URL dos nodes HTTP Request:
```
DE: http://localhost:8080/api/veiculo/nodered/evento
PARA: http://<ip-privado-ec2>:8080/api/veiculo/nodered/evento
```
Se o Node-RED estiver na **mesma EC2**, mantém `localhost:8080`.  
Se estiver em uma **EC2 separada**, usa o IP privado da VPC.

### 5. Testa o fluxo completo
```bash
# Health check
curl http://<ip-publico-ec2>:8080/health

# Login
curl -X POST http://<ip-publico-ec2>:8080/api/usuario/login \
  -H "Content-Type: application/json" \
  -d '{"email":"cliente@gmail.com","senha":"cliente123"}'

# Dashboard (com o token retornado no login)
curl http://<ip-publico-ec2>:8080/api/dashboard \
  -H "Authorization: Bearer <token>"
```
