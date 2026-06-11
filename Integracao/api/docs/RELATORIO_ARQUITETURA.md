# Relatório de Arquitetura — Toyota Experience

> Projeto: Sistema de Acompanhamento de Fabricação e Entrega de Veículos — SENAI Experience
> Versão: v1 (Spring Boot MVC + Docker Compose + Node-RED)
> Data: Junho de 2026

---

## 1. Visão Geral do Sistema

O Toyota Experience é uma plataforma integrada que conecta clientes, vendedores e a linha de produção por meio de tecnologias IIoT. A arquitetura é composta por **9 serviços containerizados** orquestrados via Docker Compose, distribuídos em quatro camadas funcionais:

```
┌──────────────────────────────────────────────────────────────┐
│                   CAMADA DE APRESENTAÇÃO                      │
│          Next.js 16 / React 19  —  porta 3000                │
│  [Cliente: Acompanhamento, Chat, Loja, Perfil, SaibaMais]    │
│  [Vendedor: Dashboard, Pedidos, Clientes, Administração]     │
│  [Login / Cadastro]                                          │
└───────────────────────────┬──────────────────────────────────┘
                            │ HTTP/REST + JWT (Bearer Token)
┌───────────────────────────▼──────────────────────────────────┐
│                   CAMADA DE NEGÓCIO                           │
│         Spring Boot 3.3.1 / Java 17  —  porta 8080           │
│  Controllers → Services → Repositories → JPA/Hibernate       │
│  Máquina de estados → StatusHistorico                        │
│  Segurança: Spring Security + JWT HS256 + BCrypt             │
└───────────┬──────────────────────────────┬───────────────────┘
            │ JDBC / JPA                    │ HTTP POST
┌───────────▼───────────┐      ┌───────────▼────────────────────┐
│   PostgreSQL 16        │      │       CAMADA IoT               │
│   porta 5432           │      │  Node-RED  —  porta 1880       │
│   db_experience        │      │  ◄── Mosquitto MQTT (1883)     │
│   pgAdmin — porta 5051 │      │  ◄── ESP32 / Simulador Python  │
└───────────────────────┘      │  ──► InfluxDB (8086)            │
                               │  ──► Grafana  (3001)            │
                               └────────────────────────────────┘
```

---

## 2. Serviços Docker Compose

| Serviço | Imagem | Porta | Função |
|---|---|---|---|
| `postgres` | postgres:16 | 5432 | Banco de dados relacional principal |
| `pgadmin` | dpage/pgadmin4 | 5051 | Administração visual do PostgreSQL |
| `api` | build local (Java/Maven) | 8080 | Backend REST + lógica de negócio |
| `chatbot` | build local (Flask/Python) | 5000 | Chatbot Totoya |
| `web` | build local (Next.js) | 3000 | Frontend cliente e vendedor |
| `mqtt` | eclipse-mosquitto:2 | 1883 | Broker de mensagens MQTT |
| `simulador` | build local (Python/paho-mqtt) | — | Simulador de linha de produção |
| `nodered` | nodered/node-red | 1880 | Middleware de integração IoT→REST |
| `influxdb` | influxdb:2.7 | 8086 | Banco de séries temporais IoT |
| `grafana` | grafana/grafana | 3001 | Dashboards de monitoramento |

---

## 3. Stack Tecnológica

### 3.1 Backend

| Componente | Tecnologia | Versão |
|---|---|---|
| Framework | Spring Boot | 3.3.1 |
| Linguagem | Java | 17 |
| Persistência | Spring Data JPA + Hibernate | herdada |
| Banco | PostgreSQL | 16 |
| Segurança | Spring Security + JWT (jjwt) | 0.11.5 |
| Validação | Jakarta Validation + Hibernate Validator | herdada |
| Build | Maven | — |
| Utilitários | Lombok | 1.18.36 |
| Documentação | Springdoc OpenAPI (Swagger UI) | 2.5.0 |
| E-mail | Spring Mail (SMTP) | herdada |

### 3.2 Frontend

| Componente | Tecnologia | Versão |
|---|---|---|
| Framework | Next.js (App Router) | ^16.2.6 |
| Biblioteca UI | React | 19.2.3 |
| Linguagem | TypeScript | ^5 |
| Estilo | Tailwind CSS | ^4 |
| HTTP Client | Axios (com interceptor JWT) | ^1.16.1 |
| Gráficos | Recharts | ^3.7.0 |
| Ícones | Lucide React | ^0.575.0 |

### 3.3 Chatbot

| Componente | Tecnologia | Versão |
|---|---|---|
| Framework | Flask | 3.1.1 |
| CORS | Flask-CORS | 5.0.1 |

### 3.4 IoT / Simulação

| Componente | Tecnologia |
|---|---|
| Broker MQTT | Eclipse Mosquitto 2 |
| Simulador Python | paho-mqtt |
| Firmware físico | C++/Arduino (ESP32) |
| Middleware | Node-RED |
| Séries temporais | InfluxDB 2.7 |
| Dashboards | Grafana |

---

## 4. Estrutura de Pacotes do Backend

```
com.senai.experience/
│
├── config/
│   ├── AppConfig.java              — BCryptPasswordEncoder bean
│   ├── DataSeeder.java             — Seed inicial: usuários, produtos, pedidos, veículos
│   └── EtapaTemplateSeeder.java    — Seed: mensagens e curiosidades por etapa de fabricação
│
├── controllers/                    — 10 controllers REST (@RestController + @RequestMapping)
│   ├── UsuarioController.java
│   ├── PedidoController.java
│   ├── VeiculoController.java
│   ├── ProdutoController.java
│   ├── StatusHistoricoController.java
│   ├── ItemPedidoController.java
│   ├── PessoaFisicaController.java
│   ├── PessoaJuridicaController.java
│   ├── EnderecoController.java
│   └── TelefoneController.java
│
├── DTO/
│   ├── request/                    — Objetos de entrada com Jakarta Validation
│   └── response/                   — Objetos de saída (projeções seguras das entidades)
│
├── entities/                       — 12 entidades JPA mapeadas para o PostgreSQL
│   ├── Usuario.java                — Superclasse (herança JOINED)
│   ├── PessoaFisica.java           — Herda Usuario; @CPF validado
│   ├── PessoaJuridica.java         — Herda Usuario; @CNPJ validado
│   ├── Pedido.java
│   ├── ItemPedido.java
│   ├── Produto.java
│   ├── Veiculo.java
│   ├── StatusHistorico.java
│   ├── Endereco.java
│   ├── Telefone.java
│   ├── EtapaTemplate.java
│   └── enums/ (UserRole, StatusFabricacao)
│
├── exception/
│   ├── ErrorResponse.java          — Estrutura padrão de resposta de erro
│   └── GlobalHandlerException.java — @ControllerAdvice central
│
├── mappers/                        — 9 mappers manuais estáticos (entity ↔ DTO)
│
├── repositories/                   — 11 interfaces Spring Data JPA
│
├── security/
│   ├── JwtUtil.java                — Geração e validação de tokens JWT HS256 (24h)
│   ├── JwtAuthFilter.java          — Filtro de autenticação por token
│   ├── SecurityConfig.java         — Spring Security + CORS
│   └── CustomUserDetailsService.java — Carrega usuário com role do banco
│
└── ExperienceApplication.java      — Ponto de entrada Spring Boot (@SpringBootApplication)
```

---

## 5. Modelo de Dados

### 5.1 Diagrama Entidade-Relacionamento

```
usuario (PK: id, nome, email, senha_hash, data_nascimento, ativo, role)
  ├── pessoa_fisica      FK: usuario.id  |  cpf VARCHAR(11) UNIQUE
  ├── pessoa_juridica    FK: usuario.id  |  cnpj VARCHAR(14) UNIQUE, razao_social
  ├── tb_endereco        FK: id_usuario  |  cep, logradouro, numero, bairro, cidade, estado
  └── tb_telefone        FK: id_usuario  |  numero VARCHAR(11)

tb_pedido (PK: id, data_pedido, valor_total)
  ├── FK: id_cliente  → usuario.id
  ├── FK: id_vendedor → usuario.id
  └── item_pedido     FK: id_pedido  |  id_produto FK → produto.id, quantidade

produto (PK: id, modelo, cor, versao, ano, preco)

tb_veiculo (PK: id, chassi INT, status_veiculo ENUM)
  ├── FK: id_produto → produto.id
  ├── FK: id_pedido  → tb_pedido.id
  └── tb_status_historico  FK: id_veiculo  |  status ENUM, data_alteracao TIMESTAMP

tb_etapa_template (PK: id, status UNIQUE, titulo, mensagem, foto_url, curiosidade)
```

### 5.2 Herança JPA — Estratégia JOINED

```
[usuario]  ←── tabela base com campos comuns
    │
    ├── [pessoa_fisica]    — cpf VARCHAR(11) UNIQUE
    │                        Validação: @CPF (Hibernate Validator)
    │
    └── [pessoa_juridica]  — cnpj VARCHAR(14) UNIQUE
                             razao_social NOT NULL
                             Validação: @CNPJ (Hibernate Validator)
```

A estratégia JOINED mantém integridade referencial e evita colunas nulas, sendo adequada para o volume de dados esperado neste contexto.

### 5.3 Enumerações

**UserRole:**
```
CLIENTE   → Acompanhamento, chat, perfil pessoal
VENDEDOR  → Dashboard, pedidos, clientes
ADMIN     → Acesso total + ativação/desativação de usuários
IOT       → Integração com dispositivos de campo
```

**StatusFabricacao (máquina de estados):**
```
AGUARDANDO           → Estado inicial após registro do veículo
MONTAGEM_ESTRUTURAL  → Soldagem e montagem do chassi e carroceria
PINTURA              → Primer, base e verniz
INSTALACAO_MOTOR     → Powertrain: motor, câmbio, suspensão
ACABAMENTO_INTERNO   → Interior, elétrica e eletrônica
INSPECAO_FINAL       → Checagem de qualidade e testes funcionais
LIBERACAO_TRANSPORTE → Aprovado; em trânsito para a concessionária
ENTREGUE             → Estado terminal — entregue ao cliente
CANCELADO            → Estado terminal — pedido ou produção cancelados
```

---

## 6. Máquina de Estados de Fabricação

```
AGUARDANDO
    │
    ▼
MONTAGEM_ESTRUTURAL
    │
    ▼
PINTURA
    │
    ▼
INSTALACAO_MOTOR
    │
    ▼
ACABAMENTO_INTERNO
    │
    ▼
INSPECAO_FINAL
    │
    ▼
LIBERACAO_TRANSPORTE
    │
    ▼
ENTREGUE  ◄── estado terminal

De qualquer estado (exceto ENTREGUE) → CANCELADO  ◄── estado terminal
```

**Regras de negócio:**
- Toda transição válida gera um registro em `tb_status_historico` com timestamp do servidor
- Transição inválida retorna HTTP 400 com mensagem descritiva
- Evento duplicado do ESP32 é tratado silenciosamente (idempotente)
- Implementada com `switch expression` do Java 17 em `StatusHistoricoService.validarTransicao()`

---

## 7. Pipeline IoT — Integração MQTT

### 7.1 Topologia

```
ESP32 (C++/Arduino)          Mosquitto Broker
linha_producao.ino  ──MQTT──►  porta 1883
10 carros × 6 etapas         tópico: senai/pii_toy
                                    │
Simulador Python ────MQTT──────────►│
(paho-mqtt)                         │
                                    ▼
                             Node-RED  (porta 1880)
                             • Subscribe no tópico
                             • Filtra: "Iniciado" / "Finalizado"
                             • Mapeia etapa → StatusFabricacao
                             • Extrai chassi: "CHASSI_00001" → 10001
                                    │
                               HTTP POST
                                    │
                                    ▼
                         Spring Boot API  (porta 8080)
                         POST /api/veiculo/nodered/evento
                         VeiculoService.mapearEtapaParaStatus()
                         StatusHistoricoService.atualizarStatus()
                                    │
                                    ├── JPA → PostgreSQL (tb_status_historico)
                                    │
                                    └── (paralelo) InfluxDB → Grafana
```

### 7.2 Payload MQTT (ESP32 / Simulador → Broker)

```json
{
  "chassi": "CHASSI_00001",
  "etapa": "MONTAGEM_ESTRUTURAL",
  "status": "Iniciado",
  "timestamp": 12345
}
```

### 7.3 Mapeamento de Etapas

| Etapa no Payload | Gatilho | StatusFabricacao gravado |
|---|---|---|
| MONTAGEM_ESTRUTURAL | Iniciado | MONTAGEM_ESTRUTURAL |
| PINTURA | Iniciado | PINTURA |
| INSTALACAO_MOTOR | Iniciado | INSTALACAO_MOTOR |
| ACABAMENTO_INTERNO | Iniciado | ACABAMENTO_INTERNO |
| INSPECAO_FINAL | Iniciado | INSPECAO_FINAL |
| LIBERACAO_TRANSPORTE | Finalizado | LIBERACAO_TRANSPORTE |

### 7.4 Firmware ESP32

- **10 veículos** em pipeline simultâneo (chassis 10001–10010)
- **6 etapas** de produção com duração configurável
- **Tick de 1 segundo** — atualização periódica do estado
- **GPIO 34** — botão start/stop da simulação
- **GPIO 32** — LED indicador de simulação ativa
- Reconexão automática para WiFi e broker MQTT
- Broker configurado: `10.109.3.9:1883` (rede SENAI)

### 7.5 Papel do Node-RED como Middleware

O Node-RED atua como camada de integração entre o broker MQTT e a API REST:

- **Desacoplamento de protocolo:** o backend não implementa cliente MQTT
- **Transformação:** filtra, mapeia e enriquece os payloads antes de encaminhar
- **Observabilidade:** fluxo visual permite monitorar o pipeline sem acesso ao código
- **Flexibilidade:** novos protocolos (Modbus, OPC-UA) podem ser integrados sem alterar o backend

### 7.6 Stack MING — Monitoramento Industrial

```
Mosquitto → Node-RED → InfluxDB (séries temporais) → Grafana (dashboards)
```

Métricas disponíveis:
- Taxa de eventos por minuto
- Distribuição de status ativos
- Tempo médio por etapa
- Histórico de throughput da linha

---

## 8. Segurança

### 8.1 Fluxo de Autenticação JWT

```
POST /api/usuario/login  →  BCrypt.matches(senha)
                         →  JwtUtil.generateToken(email, role)
                            sub: email | role: ROLE_XXX | exp: 24h | alg: HS256
                         →  { token: "eyJhbGc..." }

Requisições protegidas:
Authorization: Bearer {token}
    → JwtAuthFilter extrai e valida token
    → Popula SecurityContext com email + role
    → Controller executa com contexto autenticado
```

### 8.2 Perfis de Autorização por Endpoint

| Operação | ADMIN | VENDEDOR | CLIENTE | IOT | Público |
|---|:---:|:---:|:---:|:---:|:---:|
| Login | ✓ | ✓ | ✓ | ✓ | ✓ |
| Cadastrar usuário | ✓ | ✓ | ✓ | ✓ | ✓ |
| Listar pedidos | ✓ | ✓ | — | — | — |
| Meus pedidos | ✓ | ✓ | ✓ | — | — |
| Criar/editar pedido | ✓ | ✓ | — | — | — |
| Gerenciar produtos | ✓ | ✓ | — | — | — |
| Deletar produto | ✓ | — | — | — | — |
| Ativar/desativar usuário | ✓ | — | — | — | — |
| Evento IoT (Node-RED) | — | — | — | — | ✓ |
| Atualizar status veículo | — | — | — | — | ✓ |

### 8.3 Configuração CORS

```
Origens permitidas: http://localhost:3000, http://localhost:5173
Métodos:  GET, POST, PUT, DELETE, OPTIONS
Headers:  Authorization, Content-Type
AllowCredentials: true
```

### 8.4 Gestão de Contas

O campo `ativo` em `Usuario` permite ao ADMIN desativar contas sem deletar os dados, preservando histórico e integridade referencial. Usuários desativados recebem HTTP 403 ao autenticar.

---

## 9. API REST — Endpoints

**Base URL:** `http://localhost:8080`
**Swagger UI:** `http://localhost:8080/swagger-ui.html`
**Paginação:** `?page=0&size=10&sort=campo,desc`

### Usuários `/api/usuario`

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/usuario/login` | Login — retorna token JWT |
| POST | `/api/usuario` | Cadastrar usuário |
| GET | `/api/usuario` | Listar (paginado) |
| GET | `/api/usuario/{id}` | Buscar por ID |
| PUT | `/api/usuario/{id}` | Atualizar |
| DELETE | `/api/usuario/{id}` | Remover |
| GET | `/api/usuario/me` | Dados do usuário autenticado |
| PATCH | `/api/usuario/{id}/ativar` | Ativar conta (ADMIN) |
| PATCH | `/api/usuario/{id}/desativar` | Desativar conta (ADMIN) |

### Pedidos `/api/pedido`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/pedido` | Listar (paginado) |
| GET | `/api/pedido/{id}` | Buscar por ID |
| POST | `/api/pedido` | Criar |
| PUT | `/api/pedido/{id}` | Atualizar |
| DELETE | `/api/pedido/{id}` | Deletar |
| GET | `/api/pedido/meus-pedidos` | Pedidos do usuário logado |

### Veículos `/api/veiculo`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/veiculo` | Listar (paginado) |
| GET | `/api/veiculo/{id}` | Buscar por ID |
| POST | `/api/veiculo` | Cadastrar |
| PUT | `/api/veiculo/{id}` | Atualizar |
| DELETE | `/api/veiculo/{id}` | Remover |
| GET | `/api/veiculo/chassi/{chassi}` | Buscar por número de chassi |
| POST | `/api/veiculo/nodered/evento` | Receber evento IoT do Node-RED (público) |
| GET | `/api/veiculo/{id}/status` | Histórico completo de status |
| POST | `/api/veiculo/{id}/status` | Atualizar status via IoT direto (público) |

### Demais Recursos

| Recurso | Rota Base | Operações |
|---|---|---|
| Produtos | `/api/produto` | CRUD + paginação |
| Pessoas Físicas | `/api/pessoaFisica` | CRUD + paginação (POST público) |
| Pessoas Jurídicas | `/api/pessoaJuridica` | CRUD + paginação (POST público) |
| Endereços | `/api/endereco` | CRUD + paginação |
| Telefones | `/api/telefones` | CRUD + paginação |
| Itens de Pedido | `/api/itens-pedido` | CRUD + paginação |

### Formato de Erro Padrão

```json
{
  "timestamp": "2026-06-10T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Transição de estado inválida: ENTREGUE → PINTURA"
}
```

| Situação | HTTP |
|---|---|
| Transição de estado inválida | 400 |
| Validação de campo falhou | 400 |
| Recurso não encontrado | 404 |
| Acesso negado (role insuficiente) | 403 |
| Erro interno | 500 |

---

## 10. Frontend

### 10.1 Roteamento por Perfil

```
role ADMIN    → /Vendedor/Administracao
role VENDEDOR → /Vendedor/Dashbord
role CLIENTE  → / (página inicial)
```

### 10.2 Páginas Implementadas

| Rota | Perfil | Funcionalidade | Consome API |
|---|---|---|---|
| `/Login` | Público | Login + Cadastro com tabs | POST /api/usuario/login, POST /api/pessoaFisica |
| `/` | Cliente | Feed de notícias Toyota | — |
| `/Cliente/Acompanhamento` | Cliente | Timeline de status + RoadVisualization | GET /api/pedido/meus-pedidos, GET /api/veiculo/{id}/status |
| `/Cliente/Chat` | Cliente | Chatbot Totoya | GET/POST http://localhost:5000/api |
| `/Cliente/Loja` | Cliente | Catálogo de produtos | — |
| `/Cliente/Perfil` | Cliente | Dados pessoais | GET /api/usuario/me |
| `/Cliente/SaibaMais` | Cliente | Conteúdo informativo | — |
| `/Cliente/Apps` | Cliente | Links para aplicativos complementares | — |
| `/Vendedor/Dashbord` | Vendedor | Métricas + AreaChart + BarChart + tabela | GET /api/usuario/me, GET /api/pedido, GET /api/usuario |
| `/Vendedor/Pedidos` | Vendedor | Tabela filtrável + modal de detalhes | GET /api/pedido?size=50 |
| `/Vendedor/Clientes` | Vendedor | Listagem de clientes | GET /api/usuario |
| `/Vendedor/Administracao` | Admin | Ativação/desativação de usuários | PATCH /api/usuario/{id}/ativar |

### 10.3 Componentes Relevantes

| Componente | Descrição |
|---|---|
| `RoadVisualization` | SVG com estrada curvada (interpolação cúbica de Bézier), 10 pontos de etapa, carro na posição atual |
| `ToyotaChat` | Interface do chatbot com histórico de mensagens |
| `SideBar` | Navegação lateral por perfil |
| `Header` | Cabeçalho com dados do usuário autenticado |
| `lib/api.ts` | Axios com interceptor JWT automático em todas as requisições |
| `contexts/ThemeContext` | Contexto de tema da aplicação |

---

## 11. Chatbot Totoya (Flask)

**Arquitetura:** stateless — estado da conversa serializado no cliente a cada requisição.

### Endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/start` | Inicia conversa — retorna menu principal + estado inicial |
| POST | `/api/chat` | `{ message, state }` → `{ reply, state }` |

### Módulos de Conhecimento

- Processo produtivo (5 sub-tópicos)
- Questões financeiras (7 sub-tópicos)
- Retirada do veículo (6 sub-tópicos)
- Segurança automotiva (8 sub-tópicos)
- FAQ (5 sub-tópicos)
- Diagnóstico OBD2 — busca por código (P, B, C, U codes)

---

## 12. Dados de Seed

Populados automaticamente na primeira inicialização com banco vazio:

| Tipo | Quantidade | Detalhes |
|---|---|---|
| Admin | 1 | admin@experience.com / admin123 |
| Vendedor | 1 | vendedor.toyota@experience.com / vendedor123 |
| Clientes PF | 10 | senha: cliente123, CPFs válidos |
| Produtos | 5 | Corolla XEi 2024, Hilux SRX 2024, Yaris XLS 2024, RAV4 GR-S 2025, SW4 Diamond 2025 |
| Pedidos | 10 | Um por cliente, vinculados ao vendedor padrão |
| Veículos | 10 | Chassis 10001–10010, status inicial: AGUARDANDO |
| StatusHistorico | 10 | Estado inicial de cada veículo |
| EtapaTemplate | 9 | Mensagens e curiosidades por status de fabricação |

---

## 13. Qualidade e Testes

**Testes de integração:** 11 classes de teste (uma por controller), banco H2 em memória, MockMvc.

**Testes unitários:** `StatusHistoricoService` — cobertura de todas as transições válidas e inválidas.

**Testes manuais:** coleção Postman `IoT_Integration_Tests` com requests pré-configurados.

**Validações em múltiplas camadas:**
1. DTO Request — Jakarta Validation (`@NotNull`, `@NotBlank`, `@Email`, `@CPF`, `@CNPJ`)
2. Entidades JPA — constraints do banco (`unique=true`, `nullable=false`)
3. Lógica de negócio — máquina de estados em `StatusHistoricoService`
4. Banco de dados — constraints nativas do PostgreSQL

---

## 14. Como Executar

```bash
# Subir todos os 9 serviços
docker-compose up -d

# Verificar logs da API
docker-compose logs -f api

# Acessar os serviços
# Frontend:          http://localhost:3000
# API (Swagger):     http://localhost:8080/swagger-ui.html
# Node-RED:          http://localhost:1880
# Grafana:           http://localhost:3001
# pgAdmin:           http://localhost:5051
# InfluxDB:          http://localhost:8086
```

---

## 15. Matriz de Conformidade com Requisitos IIoT

| # | Requisito | Status | Observação |
|---|---|---|---|
| 2.1 | Arquitetura em camadas (Coleta → Comunicação → Processamento → Persistência → Apresentação) | ✅ Conforme | ESP32 (coleta), MQTT+Node-RED (comunicação), Spring Boot (processamento), PostgreSQL (persistência), Next.js (apresentação) |
| 2.2 | Baixo acoplamento — módulos independentes via API/protocolo | ✅ Conforme | Cada camada se comunica via MQTT ou REST. O backend não conhece o ESP32 diretamente. |
| 2.3 | Alta coesão — cada camada com responsabilidade única | ✅ Conforme | Frontend, backend, IoT, banco e monitoramento com responsabilidades isoladas |
| 2.4 | Escalabilidade | ⚠️ Parcial | Docker Compose permite replicar o ambiente, mas sem orquestração de múltiplas instâncias |
| 2.5 | Interoperabilidade — MQTT, JSON, REST | ✅ Conforme | MQTT com tópico definido, payloads JSON documentados, API REST mapeada |
| 3 | Visão macro: IoT → Broker → Backend → Banco → Apresentação | ✅ Conforme | Fluxo completo implementado e documentado |
| 4.1 | Camada IoT: tópico, payload, frequência | ✅ Conforme | Tópico `senai/pii_toy`, payload JSON documentado, ESP32 com tick de 1s |
| 4.2 | Camada MQTT: QoS, retenção, nomenclatura de tópicos | ⚠️ Parcial | Tópico definido; QoS e `retain` não explicitados na configuração |
| 4.3 | Camada Backend: Controller, Service, Repository | ✅ Conforme | 10 controllers, services correspondentes, 11 repositories |
| 4.4 | Camada Persistência: banco, histórico | ✅ Conforme | PostgreSQL 16 + `tb_status_historico` com histórico imutável + InfluxDB para séries temporais |
| 4.5 | Apresentação para cliente | ✅ Conforme | Next.js com timeline de status, RoadVisualization e chatbot |
| 5.1 | Integração vertical: dado bruto → informação → indicador | ✅ Conforme | ESP32 → MQTT → Node-RED → API → PostgreSQL → Frontend |
| 5.2 | Integração horizontal: API integrável | ⚠️ Parcial | API REST pública documentada no Swagger; sem mapa de integração horizontal explícito |
| 6 | Modelo híbrido: Event-Driven (MQTT) + REST síncrono | ✅ Conforme | MQTT assíncrono (ESP32 → Broker → Node-RED → API) + REST síncrono (Frontend → API) |
| 7 | Segurança: autenticação, autorização por perfil | ✅ Conforme | JWT HS256 + BCrypt + roles por endpoint + CORS configurado |
| 9.1 | Diagrama geral da arquitetura | ✅ Conforme | Diagrama ASCII presente neste documento (seção 1) |
| 9.2 | Diagrama de fluxo de dados IoT | ✅ Conforme | Diagrama de topologia presente (seção 7.1) |
| 9.5 | Estrutura de tópicos MQTT | ✅ Conforme | Tópico e payload documentados (seção 7.2 e 7.3) |
| 9.6 | Estrutura da API | ✅ Conforme | Todos os endpoints listados (seção 9) + Swagger em runtime |

### Resumo

| Status | Quantidade |
|---|---|
| ✅ Conforme | 15 |
| ⚠️ Parcial | 3 |
| ❌ Ausente | 0 |

---

*Documento atualizado em junho de 2026 — reflete a arquitetura efetivamente implementada e em execução.*
