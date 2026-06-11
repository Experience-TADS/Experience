# TOYOTA EXPERIENCE: PLATAFORMA INTEGRADA DE ACOMPANHAMENTO DE FABRICAÇÃO E ENTREGA DE VEÍCULOS COM TECNOLOGIAS IIoT

---

**Instituição:** SENAI — Serviço Nacional de Aprendizagem Industrial  
**Disciplina:** Projeto Integrador Interdisciplinar III  
**Curso:** Desenvolvimento de Sistemas  
**Data:** Junho de 2026  

---

## RESUMO

Este artigo apresenta o desenvolvimento da plataforma **Toyota Experience**, um sistema web integrado voltado ao acompanhamento em tempo real do processo de fabricação e entrega de veículos Toyota. A solução foi concebida para resolver a ausência de transparência e rastreabilidade no ciclo produtivo automotivo, conectando clientes, vendedores e a linha de produção por meio de tecnologias de Internet Industrial das Coisas (IIoT). A arquitetura adota uma abordagem de microsserviços containerizados, integrando um backend REST em Java com Spring Boot 3.3.1, um frontend reativo em Next.js 16 com React 19, um broker de mensagens MQTT com Mosquitto, um simulador de linha de produção baseado em ESP32 e Python, além de um chatbot automotivo desenvolvido em Flask. O sistema implementa uma máquina de estados formal para rastrear as etapas de fabricação, garante segurança via autenticação JWT e oferece dashboards analíticos com dados históricos persistidos em PostgreSQL 16. Os resultados demonstram que a integração de tecnologias IIoT com sistemas web modernos é viável em ambiente educacional e aplicável à indústria automotiva, promovendo maior engajamento do cliente e eficiência operacional.

**Palavras-chave:** IIoT. Spring Boot. MQTT. Rastreabilidade. Indústria 4.0. Next.js. Docker. Máquina de Estados.

---

## ABSTRACT

This paper presents the development of the **Toyota Experience** platform, an integrated web system for real-time tracking of Toyota vehicle manufacturing and delivery processes. The solution was designed to address the lack of transparency and traceability in the automotive production cycle, connecting customers, salespeople, and the production line through Industrial Internet of Things (IIoT) technologies. The architecture adopts a containerized microservices approach, integrating a REST backend in Java with Spring Boot 3.3.1, a reactive frontend in Next.js 16 with React 19, an MQTT message broker with Mosquitto, an ESP32 and Python-based production line simulator, and a Flask automotive chatbot. The system implements a formal state machine to track manufacturing stages, ensures security through JWT authentication, and offers analytical dashboards with historical data persisted in PostgreSQL 16. Results demonstrate that integrating IIoT technologies with modern web systems is feasible in an educational environment and applicable to the automotive industry, promoting greater customer engagement and operational efficiency.

**Keywords:** IIoT. Spring Boot. MQTT. Traceability. Industry 4.0. Next.js. Docker. State Machine.

---

## 1. INTRODUÇÃO

A indústria automobilística brasileira enfrenta um desafio crescente no que diz respeito à comunicação com o cliente final durante o processo de aquisição de veículos. Após a assinatura do contrato de compra, o comprador frequentemente permanece desinformado sobre o andamento da produção e da entrega do seu veículo, gerando ansiedade, insatisfação e aumento na demanda por atendimento humano nas concessionárias.

Simultaneamente, a chamada Quarta Revolução Industrial — ou Indústria 4.0 — tem impulsionado a adoção de tecnologias como IoT (Internet das Coisas), big data, automação e conectividade em tempo real nos ambientes fabris. A convergência entre o ambiente de TI (Tecnologia da Informação) e OT (Tecnologia Operacional) cria novas oportunidades para disponibilizar dados da linha de produção diretamente ao consumidor final de forma segura e compreensível.

Nesse contexto, este trabalho propõe e documenta o desenvolvimento da plataforma **Toyota Experience**, um sistema completo que:

1. Integra dispositivos IoT (microcontroladores ESP32) presentes na linha de produção com um sistema web moderno;
2. Disponibiliza ao cliente uma interface de acompanhamento em tempo real das etapas de fabricação do seu veículo;
3. Oferece ao vendedor ferramentas de gestão de pedidos, clientes e relatórios analíticos;
4. Implementa um chatbot especializado em diagnóstico automotivo e suporte ao cliente;
5. Garante rastreabilidade completa através de um histórico imutável de mudanças de status.

O sistema foi desenvolvido como Projeto Integrador Interdisciplinar III no contexto do SENAI, aplicando conceitos de engenharia de software, desenvolvimento web, IoT, segurança da informação e banco de dados em uma solução coesa e operacionalmente viável.

---

## 2. PROBLEMÁTICAS RESOLVIDAS

### 2.1 Opacidade no Processo de Fabricação

**Problema:** Após a compra de um veículo, o cliente não tinha visibilidade alguma sobre em qual etapa da linha de produção seu carro se encontrava. O único canal disponível era o contato telefônico com o vendedor, sujeito a horários e disponibilidade humana.

**Solução implementada:** A plataforma disponibiliza uma interface de acompanhamento com uma linha do tempo visual (timeline) contendo cada etapa do processo produtivo — da montagem estrutural até a entrega — atualizada automaticamente por eventos gerados pela linha de produção via MQTT. O cliente visualiza em tempo real em qual etapa seu veículo está, com descrições ilustrativas e curiosidades sobre cada fase.

### 2.2 Falta de Rastreabilidade e Auditoria

**Problema:** Não havia registro histórico das mudanças de status dos veículos, impossibilitando análises de produtividade, identificação de gargalos na linha de produção e resolução de disputas comerciais.

**Solução implementada:** Cada transição de status gera um registro na tabela `tb_status_historico` com carimbo de data e hora, criando um log imutável e auditável de toda a vida produtiva do veículo. Esse histórico é acessível via API REST e exibido no frontend.

### 2.3 Comunicação Ineficiente Entre Linha de Produção e Sistemas de TI

**Problema:** Os dados gerados pelos equipamentos da linha de produção (eventos de início e fim de etapas) não chegavam aos sistemas de gestão de clientes. Havia uma barreira tecnológica entre o chão de fábrica (OT) e os sistemas de negócio (TI).

**Solução implementada:** Foi implementado um pipeline IIoT utilizando o protocolo MQTT, padrão industrial para comunicação de baixa latência e baixo consumo. Dispositivos ESP32 publicam eventos no broker Mosquitto. O Node-RED atua como middleware de integração, filtrando, transformando e encaminhando esses eventos para a API REST do sistema via HTTP POST, eliminando o acoplamento direto entre dispositivos de campo e o backend.

### 2.4 Gestão Manual e Descentralizada de Pedidos

**Problema:** A gestão de pedidos, clientes, produtos e veículos era realizada de forma manual, fragmentada em planilhas e sem integração entre as informações cadastrais e o status de produção.

**Solução implementada:** O backend oferece uma API REST completa para gerenciamento de pedidos, clientes (pessoas físicas e jurídicas), produtos, veículos, endereços e telefones, com controle de acesso por perfil (ADMIN, VENDEDOR, CLIENTE, IOT). O frontend do vendedor inclui um dashboard analítico com gráficos de vendas, tabela de pedidos recentes e visualização de métricas.

### 2.5 Ausência de Suporte Técnico Automotivo Acessível

**Problema:** Clientes frequentemente tinham dúvidas sobre processos de entrega, documentação, manutenção e diagnóstico de falhas do veículo, demandando contato humano para questões que poderiam ser resolvidas automaticamente.

**Solução implementada:** Foi desenvolvido o chatbot **Totoya**, baseado em Flask, com menus estruturados sobre processo produtivo, questões financeiras, retirada do veículo, segurança e um catálogo de códigos de diagnóstico OBD2 automotivo. A arquitetura stateless permite escalabilidade sem estado no servidor.

### 2.6 Inexistência de Monitoramento Industrial em Tempo Real

**Problema:** Não havia dashboards ou ferramentas de monitoramento que permitissem visualizar métricas da linha de produção ao longo do tempo, dificultando decisões gerenciais baseadas em dados.

**Solução implementada:** A stack MING (Mosquitto, InfluxDB, Node-RED, Grafana) foi integrada ao sistema. Os eventos MQTT são persistidos no banco de séries temporais InfluxDB e visualizados em dashboards Grafana configuráveis, permitindo análise histórica do desempenho da linha de produção.

---

## 3. REFERENCIAL TEÓRICO

### 3.1 Indústria 4.0 e IIoT

A Indústria 4.0 representa a quarta revolução industrial, caracterizada pela integração de sistemas ciberfísicos, IoT, computação em nuvem e inteligência artificial nos processos produtivos (SCHWAB, 2016). No contexto industrial, a IIoT (Industrial Internet of Things) estende os conceitos de IoT para ambientes fabris, com requisitos adicionais de confiabilidade, determinismo e segurança.

O protocolo MQTT (Message Queuing Telemetry Transport), desenvolvido originalmente pela IBM em 1999, tornou-se o padrão de fato para comunicação em ambientes IoT devido ao seu modelo publish-subscribe, baixo overhead de protocolo e suporte a Quality of Service (QoS) configurável.

### 3.2 Arquiteturas de Microsserviços

Microsserviços são uma abordagem arquitetural onde uma aplicação é decomposta em serviços pequenos e independentes, cada um responsável por uma capacidade de negócio específica (NEWMAN, 2015). A containerização com Docker permite empacotar cada serviço com suas dependências, garantindo portabilidade e reprodutibilidade do ambiente.

### 3.3 Máquinas de Estados Finitos

Uma Máquina de Estados Finitos (FSM — Finite State Machine) é um modelo computacional que descreve o comportamento de um sistema através de um conjunto finito de estados, transições entre esses estados e ações associadas. No contexto de sistemas de fabricação, FSMs garantem que apenas transições válidas sejam executadas, preservando a integridade do processo produtivo.

### 3.4 Autenticação Baseada em Tokens JWT

JSON Web Tokens (JWT) são um padrão aberto (RFC 7519) para transmissão segura de informações entre partes como um objeto JSON assinado digitalmente. No modelo stateless, o servidor não mantém sessões, e o token carrega todas as informações necessárias para autorização, incluindo o perfil (role) do usuário.

### 3.5 Rastreabilidade em Cadeias de Valor

A rastreabilidade é definida como a capacidade de identificar e rastrear a história, aplicação ou localização de um item (ISO 9000:2015). Em cadeias de valor automotivas, a rastreabilidade é fundamental para controle de qualidade, recall de veículos e transparência com o consumidor final.

---

## 4. METODOLOGIA

O desenvolvimento seguiu a abordagem iterativa e incremental, com as seguintes fases:

**Fase 1 — Levantamento de Requisitos:** Identificação das personas (cliente, vendedor, operador IoT), mapeamento das etapas do processo produtivo Toyota e definição dos fluxos de dados entre os subsistemas.

**Fase 2 — Modelagem:** Elaboração do diagrama de classes UML, modelo entidade-relacionamento do banco de dados, definição dos endpoints REST e especificação do fluxo MQTT end-to-end.

**Fase 3 — Implementação:** Desenvolvimento paralelo do backend, frontend, firmware ESP32, simulador Python e chatbot Flask, integrados via docker-compose.

**Fase 4 — Integração e Testes:** Validação dos endpoints via Postman (coleção `IoT_Integration_Tests`), testes de integração com banco H2 em memória e testes da máquina de estados.

**Fase 5 — Documentação:** Produção de documentação técnica, guias de uso e este artigo científico.

---

## 5. ARQUITETURA DO SISTEMA

### 5.1 Visão Geral

A plataforma Toyota Experience é composta por **nove serviços** orquestrados via Docker Compose, distribuídos em quatro camadas funcionais:

```
┌─────────────────────────────────────────────────────────────┐
│                    CAMADA DE APRESENTAÇÃO                    │
│         Next.js 16 / React 19 (porta 3000)                  │
│   [Cliente: Acompanhamento, Chat, Loja, Perfil]             │
│   [Vendedor: Dashboard, Pedidos, Clientes, Admin]           │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST + JWT
┌────────────────────────▼────────────────────────────────────┐
│                    CAMADA DE NEGÓCIO                         │
│         Spring Boot 3.3.1 / Java 17 (porta 8080)            │
│   [Controllers → Services → Repositories → JPA/Hibernate]  │
│   [Máquina de Estados → StatusHistorico → PostgreSQL]       │
└──────────┬─────────────────────────────┬────────────────────┘
           │ JDBC                         │ HTTP POST
┌──────────▼──────────┐        ┌──────────▼──────────────────┐
│  PostgreSQL 16      │        │  CAMADA IoT                  │
│  (porta 5432)       │        │  Node-RED (1880)             │
│  db_experience      │        │  ◄── Mosquitto MQTT (1883)  │
└─────────────────────┘        │  ◄── ESP32 / Simulador Py   │
                               │  ──► InfluxDB (8086)         │
                               │  ──► Grafana (3001)          │
                               └─────────────────────────────┘
```

### 5.2 Serviços Docker Compose

| Serviço | Imagem | Porta | Função |
|---|---|---|---|
| `postgres` | postgres:16 | 5432 | Banco de dados relacional principal |
| `pgadmin` | dpage/pgadmin4 | 5051 | Administração visual do PostgreSQL |
| `api` | build local (Java) | 8080 | Backend REST + lógica de negócio |
| `chatbot` | build local (Flask) | 5000 | Chatbot Totoya |
| `web` | build local (Next.js) | 3000 | Frontend cliente e vendedor |
| `mqtt` | eclipse-mosquitto:2 | 1883 | Broker de mensagens MQTT |
| `simulador` | build local (Python) | — | Simulador de linha de produção |
| `nodered` | nodered/node-red | 1880 | Middleware de integração IoT→REST |
| `influxdb` | influxdb:2.7 | 8086 | Banco de séries temporais IoT |
| `grafana` | grafana/grafana | 3001 | Dashboards de monitoramento |

### 5.3 Estrutura de Pacotes do Backend

```
com.senai.experience/
├── config/
│   ├── AppConfig.java          — Configurações gerais (BCryptPasswordEncoder)
│   ├── DataSeeder.java         — Seed inicial de dados (usuários, produtos, pedidos, veículos)
│   └── EtapaTemplateSeeder.java — Seed das mensagens e curiosidades por etapa
├── controllers/                — 10 controllers REST (@RestController)
├── DTO/
│   ├── request/               — Objetos de entrada validados com Jakarta Validation
│   └── response/              — Objetos de saída (projeções seguras das entidades)
├── entities/                  — 12 entidades JPA mapeadas para o PostgreSQL
├── exception/
│   ├── ErrorResponse.java     — Estrutura padrão de resposta de erro
│   └── GlobalHandlerException.java — @ControllerAdvice central
├── mappers/                   — 9 mappers manuais estáticos (sem MapStruct)
├── repositories/              — 11 interfaces Spring Data JPA
├── security/
│   ├── JwtUtil.java           — Geração e validação de tokens JWT HS256
│   ├── JwtAuthFilter.java     — Filtro de autenticação por token
│   ├── SecurityConfig.java    — Configuração Spring Security + CORS
│   └── CustomUserDetailsService.java — Carrega usuário com role do banco
└── ExperienceApplication.java — Ponto de entrada Spring Boot
```

---

## 6. MODELO DE DADOS

### 6.1 Diagrama Entidade-Relacionamento

O banco de dados `db_experience` é composto por **11 tabelas** principais com os seguintes relacionamentos:

```
usuario (PK: id)
  ├── pessoa_fisica      → FK: usuario.id | CPF único, validado
  ├── pessoa_juridica    → FK: usuario.id | CNPJ único, razão social
  ├── tb_endereco        → FK: id_usuario (1:N)
  └── tb_telefone        → FK: id_usuario (1:N)

tb_pedido (PK: id)
  ├── FK: id_cliente → usuario.id
  ├── FK: id_vendedor → usuario.id
  └── item_pedido (1:N)
       └── FK: id_produto → produto.id

tb_veiculo (PK: id)
  ├── FK: id_produto → produto.id
  ├── FK: id_pedido → tb_pedido.id
  ├── chassi: INT (identificador físico único)
  ├── status_veiculo: ENUM (StatusFabricacao)
  └── tb_status_historico (1:N)
       └── status: ENUM | data_alteracao: TIMESTAMP

tb_etapa_template (PK: id)
  └── status UNIQUE, titulo, mensagem, foto_url, curiosidade

produto (PK: id)
  └── modelo, cor, versao, ano, preco
```

### 6.2 Hierarquia de Usuário (Herança JPA JOINED)

A estratégia de herança JOINED foi adotada para representar os diferentes tipos de usuário do sistema, mantendo integridade referencial e evitando colunas nulas:

```
[usuario]           — tabela base: id, nome, email, senha_hash,
                      data_nascimento, ativo, role
      │
      ├── [pessoa_fisica]    — cpf VARCHAR(11) UNIQUE
      │                        Validação: @CPF (Hibernate Validator)
      │
      └── [pessoa_juridica]  — cnpj VARCHAR(14) UNIQUE
                               razao_social NOT NULL
                               Validação: @CNPJ (Hibernate Validator)
```

### 6.3 Enumerações do Sistema

**UserRole** — perfis de acesso:
```
CLIENTE   → Acesso ao acompanhamento, chat e perfil pessoal
VENDEDOR  → Acesso ao dashboard, pedidos e clientes
ADMIN     → Acesso total, incluindo ativação/desativação de usuários
IOT       → Usuário técnico para integração com dispositivos de campo
```

**StatusFabricacao** — estados da máquina de fabricação:
```
AGUARDANDO             → Veículo registrado, aguardando início da produção
MONTAGEM_ESTRUTURAL    → Soldagem e montagem do chassi e carroceria
PINTURA                → Aplicação de primer, base e verniz
INSTALACAO_MOTOR       → Montagem do powertrain (motor, câmbio, suspensão)
ACABAMENTO_INTERNO     → Instalação de interior, elétrica e eletrônica
INSPECAO_FINAL         → Checagem de qualidade e testes funcionais
LIBERACAO_TRANSPORTE   → Veículo aprovado e em trânsito para a concessionária
ENTREGUE               → Veículo entregue ao cliente
CANCELADO              → Pedido ou produção cancelados
```

---

## 7. MÁQUINA DE ESTADOS DE FABRICAÇÃO

### 7.1 Diagrama de Transições

```
                    ┌─────────────────┐
                    │   AGUARDANDO    │◄─── Estado inicial
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  MONTAGEM_      │
                    │  ESTRUTURAL     │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │    PINTURA      │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  INSTALACAO_    │
                    │    MOTOR        │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  ACABAMENTO_    │
                    │    INTERNO      │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │   INSPECAO_     │
                    │    FINAL        │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  LIBERACAO_     │
                    │  TRANSPORTE     │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │    ENTREGUE     │◄─── Estado terminal
                    └─────────────────┘

De qualquer estado (exceto ENTREGUE) → CANCELADO (estado terminal)
```

### 7.2 Regras de Negócio

- Cada transição válida gera automaticamente um novo registro em `tb_status_historico` com timestamp do servidor;
- Tentativa de transição inválida retorna HTTP 400 com mensagem descritiva;
- Recebimento de status duplicado (evento repetido do ESP32) é tratado silenciosamente, retornando o último registro sem gerar erro;
- Os estados `ENTREGUE` e `CANCELADO` são terminais — nenhuma transição é permitida a partir deles;
- A implementação utiliza `switch expression` do Java 17, garantindo cobertura exaustiva de casos em tempo de compilação.

### 7.3 Impacto no Negócio

A máquina de estados formal elimina a possibilidade de inconsistências como um veículo passar diretamente de `AGUARDANDO` para `ENTREGUE`, ou retroceder no processo. Isso garante que o cliente sempre visualize informações coerentes com a realidade da linha de produção.

---

## 8. INTEGRAÇÃO IIoT — PIPELINE MQTT

### 8.1 Topologia Completa

```
┌─────────────────────┐    MQTT Publish     ┌─────────────────────┐
│   ESP32 (C++/Arduino)│──────────────────►  │                     │
│   linha_producao.ino │    Tópico:          │  Mosquitto Broker   │
│   10 carros × 6 etapas│  senai/pii_toy    │  (porta 1883)       │
└─────────────────────┘                     └──────────┬──────────┘
                                                       │ MQTT Subscribe
┌─────────────────────┐    MQTT Publish               │
│  Simulador Python   │──────────────────────────────►│
│  (paho-mqtt)        │                               ▼
└─────────────────────┘                    ┌─────────────────────┐
                                           │    Node-RED (1880)   │
                                           │  Filtro: status      │
                                           │  "Iniciado"/"Final.."│
                                           │  Mapeamento etapa    │
                                           │  → StatusFabricacao  │
                                           │  Extração chassi:    │
                                           │  "CHASSI_00001"→10001│
                                           └──────────┬──────────┘
                                                      │ HTTP POST
                                           ┌──────────▼──────────┐
                                           │  Spring Boot API    │
                                           │  POST /api/veiculo/ │
                                           │  nodered/evento     │
                                           └──────────┬──────────┘
                                                      │ JPA
                                           ┌──────────▼──────────┐
                                           │    PostgreSQL 16     │
                                           │  tb_status_historico │
                                           └─────────────────────┘
                                                      │ Paralelo
                                           ┌──────────▼──────────┐
                                           │     InfluxDB 2.7     │
                                           │  Séries temporais    │
                                           └──────────┬──────────┘
                                                      │
                                           ┌──────────▼──────────┐
                                           │      Grafana         │
                                           │  Dashboards IoT      │
                                           └─────────────────────┘
```

### 8.2 Formato do Payload MQTT

Cada evento publicado pelo ESP32 ou simulador segue o formato JSON:

```json
{
  "chassi": "CHASSI_00001",
  "etapa": "MONTAGEM_ESTRUTURAL",
  "status": "Iniciado",
  "timestamp": 12345
}
```

### 8.3 Mapeamento de Etapas para Status

O Node-RED é responsável por converter o vocabulário da linha de produção no vocabulário do sistema:

| Etapa ESP32 | Gatilho | StatusFabricacao |
|---|---|---|
| MONTAGEM_ESTRUTURAL | Iniciado | MONTAGEM_ESTRUTURAL |
| PINTURA | Iniciado | PINTURA |
| INSTALACAO_MOTOR | Iniciado | INSTALACAO_MOTOR |
| ACABAMENTO_INTERNO | Iniciado | ACABAMENTO_INTERNO |
| INSPECAO_FINAL | Iniciado | INSPECAO_FINAL |
| LIBERACAO_TRANSPORTE | Finalizado | LIBERACAO_TRANSPORTE |

### 8.4 Firmware ESP32

O firmware `linha_producao.ino` simula uma linha de produção industrial com as seguintes características:

- **10 veículos em pipeline simultâneo** (chassis 10001 a 10010);
- **6 etapas de produção** com duração configurável por etapa;
- **Tick de 1 segundo** — atualização periódica do estado de cada veículo;
- **Botão GPIO 34** — start/stop da simulação;
- **LED GPIO 32** — indicador visual de simulação ativa;
- **Reconexão automática** para WiFi e broker MQTT em caso de queda;
- **Broker configurado** para rede SENAI: `10.109.3.9:1883`.

### 8.5 Papel do Node-RED como Middleware

A escolha do Node-RED como camada de integração entre o broker MQTT e a API REST foi deliberada e justifica-se pelos seguintes fatores:

1. **Desacoplamento de protocolos:** o backend Spring Boot não precisa implementar um cliente MQTT, evitando complexidade adicional e dependências;
2. **Transformação de dados:** o Node-RED realiza filtragem, mapeamento e enriquecimento dos payloads antes de encaminhá-los;
3. **Observabilidade:** o fluxo visual do Node-RED permite monitorar e depurar o pipeline de eventos sem acesso ao código-fonte;
4. **Flexibilidade:** novas fontes de eventos (outros protocolos industriais como Modbus, OPC-UA) podem ser integradas sem alterar o backend.

---

## 9. SEGURANÇA E CONTROLE DE ACESSO

### 9.1 Fluxo de Autenticação JWT

```
Cliente HTTP              Spring Boot API              PostgreSQL
     │                          │                           │
     │  POST /api/usuario/login │                           │
     │  { email, senha }        │                           │
     │─────────────────────────►│                           │
     │                          │  SELECT * FROM usuario    │
     │                          │  WHERE email = ?          │
     │                          │──────────────────────────►│
     │                          │◄──────────────────────────│
     │                          │  BCrypt.matches(senha)    │
     │                          │  JwtUtil.generateToken()  │
     │                          │  - sub: email             │
     │                          │  - role: ROLE_CLIENTE      │
     │                          │  - exp: agora + 24h        │
     │                          │  - alg: HS256              │
     │◄─────────────────────────│                           │
     │  { token: "eyJhbG..." }  │                           │
     │                          │                           │
     │  GET /api/pedido/meus-pedidos                        │
     │  Authorization: Bearer eyJhbG...                     │
     │─────────────────────────►│                           │
     │                          │  JwtAuthFilter:           │
     │                          │  - Extrai token           │
     │                          │  - Valida assinatura      │
     │                          │  - Extrai role            │
     │                          │  - Popula SecurityContext │
     │◄─────────────────────────│                           │
     │  { pedidos: [...] }      │                           │
```

### 9.2 Perfis de Autorização por Endpoint

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
| Atualizar status (IoT) | — | — | — | — | ✓ |

### 9.3 Configuração CORS

Para integração segura entre frontend e backend:

- **Origens permitidas:** `http://localhost:3000`, `http://localhost:5173`
- **Métodos permitidos:** GET, POST, PUT, DELETE, OPTIONS
- **Headers permitidos:** Authorization, Content-Type
- **AllowCredentials:** true

### 9.4 Gestão de Usuários Ativos

O campo `ativo` na entidade `Usuario` permite ao ADMIN desativar contas sem deletá-las do banco, preservando o histórico e integridade referencial. Usuários desativados recebem HTTP 403 ao tentar autenticar.

---

## 10. API REST — REFERÊNCIA DE ENDPOINTS

**Base URL:** `http://localhost:8080`  
**Documentação interativa:** `http://localhost:8080/swagger-ui.html`  
**Formato:** JSON (Content-Type: application/json)  
**Paginação:** todos os endpoints de listagem suportam `?page=0&size=10&sort=campo,desc`

### 10.1 Usuários `/api/usuario`

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/usuario/login` | Autenticação — retorna token JWT |
| POST | `/api/usuario` | Cadastrar novo usuário |
| GET | `/api/usuario` | Listar usuários (paginado) |
| GET | `/api/usuario/{id}` | Buscar usuário por ID |
| PUT | `/api/usuario/{id}` | Atualizar dados do usuário |
| DELETE | `/api/usuario/{id}` | Remover usuário |
| GET | `/api/usuario/me` | Dados do usuário autenticado |
| PATCH | `/api/usuario/{id}/ativar` | Ativar conta (ADMIN) |
| PATCH | `/api/usuario/{id}/desativar` | Desativar conta (ADMIN) |

### 10.2 Pedidos `/api/pedido`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/pedido` | Listar pedidos (paginado) |
| GET | `/api/pedido/{id}` | Buscar pedido por ID |
| POST | `/api/pedido` | Criar pedido |
| PUT | `/api/pedido/{id}` | Atualizar pedido |
| DELETE | `/api/pedido/{id}` | Deletar pedido |
| GET | `/api/pedido/meus-pedidos` | Pedidos do usuário logado |

### 10.3 Veículos `/api/veiculo`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/veiculo` | Listar veículos (paginado) |
| GET | `/api/veiculo/{id}` | Buscar veículo por ID |
| POST | `/api/veiculo` | Cadastrar veículo |
| PUT | `/api/veiculo/{id}` | Atualizar veículo |
| DELETE | `/api/veiculo/{id}` | Remover veículo |
| GET | `/api/veiculo/chassi/{chassi}` | Buscar por número de chassi |
| POST | `/api/veiculo/nodered/evento` | Receber evento IoT do Node-RED |
| GET | `/api/veiculo/{id}/status` | Histórico completo de status |
| POST | `/api/veiculo/{id}/status` | Atualizar status (IoT direto) |

### 10.4 Demais Recursos

| Recurso | Rota Base | Operações |
|---|---|---|
| Produtos | `/api/produto` | CRUD completo + paginação |
| Pessoas Físicas | `/api/pessoaFisica` | CRUD + paginação (POST público) |
| Pessoas Jurídicas | `/api/pessoaJuridica` | CRUD + paginação (POST público) |
| Endereços | `/api/endereco` | CRUD + paginação |
| Telefones | `/api/telefones` | CRUD + paginação |
| Itens de Pedido | `/api/itens-pedido` | CRUD + paginação |

### 10.5 Tratamento de Erros

Todos os erros seguem o formato padronizado `ErrorResponse`:

```json
{
  "timestamp": "2026-06-10T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Transição de estado inválida: ENTREGUE → PINTURA"
}
```

| Situação | HTTP | Descrição |
|---|---|---|
| Transição de estado inválida | 400 | Violação da máquina de estados |
| Validação de campo falhou | 400 | @NotNull, @CPF, @Email, etc. |
| Recurso não encontrado | 404 | Entidade não existe no banco |
| Acesso negado | 403 | Role insuficiente para a operação |
| Erro interno | 500 | Exceção não tratada |

---

## 11. FRONTEND

### 11.1 Tecnologias e Bibliotecas

| Biblioteca | Versão | Função |
|---|---|---|
| Next.js | ^16.2.6 | Framework React com App Router e SSR |
| React | 19.2.3 | Biblioteca de UI |
| TypeScript | ^5 | Tipagem estática |
| Tailwind CSS | ^4 | Estilização utilitária |
| Axios | ^1.16.1 | Cliente HTTP com interceptor JWT |
| Recharts | ^3.7.0 | Gráficos do dashboard do vendedor |
| Lucide React | ^0.575.0 | Biblioteca de ícones |

### 11.2 Roteamento por Perfil

Após o login, o sistema redireciona automaticamente o usuário para a área correspondente ao seu perfil:

```
role: ADMIN    → /Vendedor/Administracao
role: VENDEDOR → /Vendedor/Dashbord
role: CLIENTE  → / (página inicial)
```

### 11.3 Área do Cliente

**Página de Acompanhamento (`/Cliente/Acompanhamento`)**

É o coração da experiência do cliente. Exibe:
- Lista de pedidos ativos do cliente;
- Para cada pedido, o veículo vinculado e seu status atual;
- Uma linha do tempo visual com todas as etapas do processo;
- Para cada etapa concluída: data/hora da transição, mensagem descritiva e curiosidade sobre aquela fase da produção;
- **RoadVisualization:** componente SVG exclusivo que renderiza uma estrada curvada com 10 pontos de passagem, posicionando um ícone de carro na etapa atual. A trajetória é calculada por interpolação cúbica de Bézier, criando uma experiência visual imersiva e única.

**Chat Totoya (`/Cliente/Chat`)**

Interface conversacional com o chatbot automotivo:
- Menus estruturados em árvore de opções;
- Módulos: processo produtivo, questões financeiras, retirada do veículo, segurança, FAQ;
- Catálogo de diagnóstico OBD2 com busca por código;
- Interface responsiva com histórico de mensagens.

**Outras páginas do cliente:**
- `/Cliente/Loja` — Catálogo de produtos Toyota
- `/Cliente/Perfil` — Dados pessoais e preferências
- `/Cliente/SaibaMais` — Conteúdo informativo sobre a Toyota
- `/Cliente/Apps` — Links para aplicativos complementares

### 11.4 Área do Vendedor

**Dashboard (`/Vendedor/Dashbord`)**
- Card de boas-vindas com nome do vendedor autenticado;
- Métricas gerais: total de vendas, número de clientes, receita;
- `AreaChart` (Recharts) — evolução de vendas ao longo do tempo;
- `BarChart` (Recharts) — top veículos mais vendidos por modelo;
- Tabela de pedidos recentes com status e cliente.

**Pedidos (`/Vendedor/Pedidos`)**
- Tabela filtrável com todos os pedidos;
- Modal de detalhes por pedido;
- Informações do cliente, produtos, valor total e status do veículo.

**Clientes (`/Vendedor/Clientes`)**
- Listagem paginada de clientes;
- Visualização de dados cadastrais.

**Administração (`/Vendedor/Administracao`)**
- Painel exclusivo para perfil ADMIN;
- Ativação e desativação de contas de usuários.

### 11.5 Interceptor JWT (lib/api.ts)

O cliente Axios foi configurado com um interceptor que injeta automaticamente o token JWT no header `Authorization: Bearer {token}` de todas as requisições autenticadas, simplificando o gerenciamento de autenticação no frontend.

---

## 12. CHATBOT TOTOYA

### 12.1 Arquitetura

O Totoya é implementado em Flask com arquitetura **stateless**: o estado da conversa é serializado como objeto JSON e enviado pelo cliente a cada mensagem, retornado atualizado na resposta. Isso elimina a necessidade de gerenciamento de sessões no servidor e torna o chatbot horizontalmente escalável.

### 12.2 Endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/start` | Inicia conversa — retorna menu principal e estado inicial |
| POST | `/api/chat` | Envia mensagem — recebe `{ message, state }`, retorna `{ reply, state }` |

### 12.3 Árvore de Menus

```
Menu Principal
├── 1. Processo Produtivo
│   ├── Etapas de fabricação
│   ├── Tempo médio por etapa
│   ├── Controle de qualidade
│   ├── Rastreamento do veículo
│   └── Comunicação com cliente
├── 2. Questões Financeiras
│   ├── Financiamento
│   ├── Formas de pagamento
│   ├── Seguro do veículo
│   ├── IPVA e licenciamento
│   ├── Revisões e garantias
│   ├── Consórcio
│   └── Valor de revenda
├── 3. Retirada do Veículo
│   ├── Documentação necessária
│   ├── Vistoria de entrega
│   ├── Personalização
│   ├── Acessórios opcionais
│   ├── Treinamento de uso
│   └── Serviços pós-venda
├── 4. Segurança
│   ├── Sistemas de segurança ativa
│   ├── Airbags e proteções passivas
│   ├── Toyota Safety Sense
│   ├── Testes de colisão
│   ├── Segurança conectada
│   ├── Manutenção preventiva
│   ├── Dirigibilidade segura
│   └── Segurança para crianças
├── 5. FAQ
│   ├── Prazo de entrega
│   ├── Modificações no pedido
│   ├── Cancelamento
│   ├── Acompanhamento online
│   └── Contato com suporte
└── 6. Código OBD2
    └── [busca livre por código: P0300, B0100, etc.]
```

### 12.4 Catálogo OBD2 Implementado

| Código | Categoria | Diagnóstico |
|---|---|---|
| P0100 | Powertrain | Sensor de fluxo de massa de ar (MAF) |
| P0115 | Powertrain | Sensor de temperatura do líquido de arrefecimento |
| P0120 | Powertrain | Sensor de posição da borboleta (TPS) |
| P0171 | Powertrain | Mistura pobre no banco 1 |
| P0300–P0304 | Powertrain | Falha de ignição nos cilindros 1 a 4 |
| P0420 | Powertrain | Eficiência do catalisador abaixo do limiar |
| P0500 | Powertrain | Sensor de velocidade do veículo |
| P0700 | Powertrain | Sistema de controle da transmissão |
| P1604 | Powertrain | Partida travada (Toyota específico) |
| P1605 | Powertrain | Falha no módulo de controle de knockdown |
| B0100 | Carroceria | Sistema de airbag — circuito aberto |
| B2799 | Carroceria | Sistema imobilizador — incompatibilidade |
| C1201 | Chassi | Sistema de freios ABS — falha no motor |
| C1231 | Chassi | Sensor de velocidade da roda traseira direita |
| U0100 | Rede | Comunicação perdida com ECM/PCM |
| U0129 | Rede | Comunicação perdida com módulo ABS |

---

## 13. DADOS DE SEED — AMBIENTE INICIAL

Para facilitar o desenvolvimento, testes e demonstração, o sistema popula automaticamente o banco de dados na primeira execução:

### 13.1 Usuários Padrão

| Perfil | Email | Senha | Observações |
|---|---|---|---|
| Admin | admin@experience.com | admin123 | Acesso total ao sistema |
| Vendedor | vendedor.toyota@experience.com | vendedor123 | Gestão de pedidos e clientes |
| 10 Clientes PF | (nomes variados) | cliente123 | Dados com CPFs válidos |

### 13.2 Catálogo de Produtos

| Modelo | Versão | Ano | Preço |
|---|---|---|---|
| Corolla | XEi | 2024 | R$ 149.900,00 |
| Hilux | SRX | 2024 | R$ 289.900,00 |
| Yaris | XLS | 2024 | R$ 109.900,00 |
| RAV4 | GR-S | 2025 | R$ 319.900,00 |
| SW4 | Diamond | 2025 | R$ 399.900,00 |

### 13.3 Pedidos e Veículos

- **10 pedidos** — um por cliente, vinculados ao vendedor padrão;
- **10 veículos** — chassis 10001 a 10010, todos com status inicial `AGUARDANDO`;
- **10 registros** em `tb_status_historico` com o estado inicial de cada veículo.

---

## 14. MONITORAMENTO — STACK MING

A plataforma implementa a stack **MING** (Mosquitto, InfluxDB, Node-RED, Grafana) para monitoramento industrial em tempo real:

### 14.1 Fluxo de Dados para Monitoramento

```
Eventos MQTT → Node-RED → InfluxDB (séries temporais)
                                    │
                               Grafana ← Consultas InfluxQL/Flux
                                    │
                        Dashboards de monitoramento:
                        - Taxa de eventos por minuto
                        - Distribuição de status ativos
                        - Tempo médio por etapa
                        - Histórico de throughput da linha
```

### 14.2 Justificativa Técnica

O InfluxDB foi escolhido por ser um banco de dados otimizado para séries temporais, com compressão nativa de dados temporais e consultas de agregação eficientes. O Grafana oferece visualização flexível com alertas configuráveis, painel de alertas e compartilhamento de dashboards.

---

## 15. QUALIDADE E TESTES

### 15.1 Estratégia de Testes

**Testes de Integração:** 11 classes de teste, uma por controller REST, utilizando:
- Spring Boot Test com `@SpringBootTest`
- Banco H2 em memória via `application-test.properties`
- MockMvc para simulação de requisições HTTP
- Cobertura de cenários de sucesso e de erro

**Testes Unitários:** Classe dedicada para `StatusHistoricoService`, validando todas as transições válidas e inválidas da máquina de estados.

**Testes Manuais:** Coleção Postman `IoT_Integration_Tests` com requests pré-configurados para todos os endpoints, incluindo cenários de integração IoT.

### 15.2 Validação de Dados

O sistema aplica validações em múltiplas camadas:

- **DTO Request:** Jakarta Validation (`@NotNull`, `@NotBlank`, `@Email`, `@Size`, `@CPF`, `@CNPJ`)
- **Entidades JPA:** constraints de banco (`@Column(unique=true)`, `nullable=false`)
- **Lógica de negócio:** validação da máquina de estados em `StatusHistoricoService`
- **Banco de dados:** constraints nativas do PostgreSQL

---

## 16. IMPLANTAÇÃO E EXECUÇÃO

### 16.1 Pré-requisitos

- Docker Desktop (Windows/Linux/macOS)
- Docker Compose v2
- Porta 3000 (web), 8080 (API), 5432 (DB), 1883 (MQTT), 1880 (Node-RED), 3001 (Grafana) disponíveis

### 16.2 Execução Completa

```bash
# Clonar o repositório
git clone [repositório]
cd Integracao

# Subir todos os 9 serviços
docker-compose up -d

# Verificar logs da API
docker-compose logs -f api

# Acessar o sistema
# Frontend:    http://localhost:3000
# API (Swagger): http://localhost:8080/swagger-ui.html
# Node-RED:    http://localhost:1880
# Grafana:     http://localhost:3001
# pgAdmin:     http://localhost:5051
```

### 16.3 Credenciais de Acesso

| Sistema | URL | Usuário | Senha |
|---|---|---|---|
| Frontend (Admin) | localhost:3000 | admin@experience.com | admin123 |
| Frontend (Vendedor) | localhost:3000 | vendedor.toyota@experience.com | vendedor123 |
| pgAdmin | localhost:5051 | (configurado no compose) | (configurado no compose) |
| Grafana | localhost:3001 | admin | (configurado no compose) |

---

## 17. CONTRIBUIÇÕES TÉCNICAS

Este trabalho apresenta as seguintes contribuições originais:

**1. Middleware IIoT com Node-RED para desacoplamento de protocolos**
Demonstra uma arquitetura onde o Node-RED atua como camada de tradução entre o protocolo MQTT (chão de fábrica) e REST HTTP (sistema de negócio), sem necessidade de implementar um cliente MQTT no backend Java. Esta abordagem reduz a complexidade do backend e aumenta a flexibilidade para integração com diferentes protocolos industriais.

**2. Máquina de estados formal para rastreabilidade de manufatura em ambiente educacional**
Implementação de uma FSM com `switch expression` Java 17 integrada a um banco de dados relacional, gerando trilha de auditoria completa. O modelo é extensível para outros domínios com processos sequenciais controlados.

**3. Containerização completa de ambiente IIoT para laboratório educacional**
Todos os componentes — desde o backend até o simulador IoT e os dashboards — são orquestrados via Docker Compose, permitindo que qualquer aluno ou professor replique o ambiente completo com um único comando, sem instalação manual de dependências.

**4. Chatbot automotivo stateless com OBD2 integrado**
Demonstra que um chatbot de suporte técnico automotivo pode ser implementado de forma stateless (sem sessão no servidor), com base de conhecimento de diagnóstico OBD2, adequado para uso em concessionárias com alto volume de atendimento.

**5. Firmware ESP32 como gêmeo digital de linha de produção**
O firmware `linha_producao.ino` simula 10 veículos em 6 etapas de fabricação paralelas com controle por botão físico e indicador LED, demonstrando como microcontroladores de baixo custo podem representar fielmente o comportamento de uma linha industrial para fins de prototipagem e ensino.

---

## 18. DISCUSSÃO

### 18.1 Viabilidade da Integração IIoT em PMEs

A arquitetura proposta demonstra que a integração IIoT não requer infraestrutura de grande porte. Utilizando hardware de baixo custo (ESP32: ~R$ 30,00) e software open source (Mosquitto, Node-RED, Grafana), é possível implementar um sistema de rastreabilidade completo acessível a pequenas e médias empresas do setor automotivo.

### 18.2 Transparência como Diferencial Competitivo

A capacidade de o cliente acompanhar em tempo real a fabricação do seu veículo representa um diferencial competitivo significativo. Estudos de experiência do cliente indicam que a percepção de transparência e controle reduz a ansiedade durante processos de espera e aumenta a satisfação pós-compra (ZEITHAML et al., 2018).

### 18.3 Limitações do Projeto

- O sistema atual não implementa notificações push (WebSocket ou SSE) — o cliente precisa atualizar manualmente a página para ver novas atualizações;
- A autenticação OAuth2 está estruturalmente preparada mas não implementada;
- O firmware ESP32 opera em rede local SENAI e requer reconfiguração do IP do broker para ambientes diferentes;
- Não há implementação de HTTPS/TLS no broker MQTT, recomendado para ambientes de produção;
- O catálogo OBD2 é estático; uma versão de produção deveria consultar bases de dados atualizadas por fabricante.

### 18.4 Trabalhos Futuros

- Implementação de WebSocket para atualizações em tempo real no frontend sem necessidade de polling;
- Integração com OAuth2 (Google, Microsoft) para autenticação social;
- Implementação de TLS/SSL no broker MQTT e na API;
- Expansão do chatbot com NLP (Processamento de Linguagem Natural) utilizando modelos de linguagem;
- Integração com APIs de geolocalização para rastreamento do veículo durante transporte;
- Implementação de notificações push via Firebase Cloud Messaging;
- Módulo de assinatura digital de documentos de entrega.

---

## 19. CONCLUSÃO

A plataforma Toyota Experience demonstrou ser tecnicamente viável e funcionalmente completa para o objetivo proposto: conectar o cliente final ao processo de fabricação do seu veículo em tempo real, por meio de tecnologias IIoT acessíveis e uma arquitetura de software moderna.

Os problemas centrais identificados — opacidade no processo produtivo, falta de rastreabilidade, desconexão entre linha de produção e sistemas de TI, e ausência de suporte técnico automatizado — foram endereçados por soluções específicas e integradas em uma plataforma coesa.

A escolha tecnológica (Spring Boot, Next.js, MQTT, Docker, PostgreSQL) alinha-se com o mercado de trabalho atual e demonstra aplicação prática dos conteúdos do curso de Desenvolvimento de Sistemas do SENAI. A containerização completa via Docker Compose torna o projeto reproduzível, documentado e pronto para demonstração, características essenciais tanto em contextos acadêmicos quanto profissionais.

A integração de um dispositivo físico ESP32 com um sistema web completo — passando por um broker MQTT, middleware Node-RED, API REST Java e frontend React — exemplifica concretamente os princípios da Indústria 4.0 aplicados ao setor automotivo, contribuindo para a formação de profissionais aptos a desenvolver soluções de integração industrial no mercado nacional.

---

## REFERÊNCIAS

NEWMAN, S. **Building Microservices: Designing Fine-Grained Systems**. Sebastopol: O'Reilly Media, 2015.

SCHWAB, K. **A Quarta Revolução Industrial**. São Paulo: Edipro, 2016.

ASSOCIAÇÃO BRASILEIRA DE NORMAS TÉCNICAS. **NBR ISO 9000:2015**: Sistemas de gestão da qualidade — Fundamentos e vocabulário. Rio de Janeiro: ABNT, 2015.

INTERNATIONAL ORGANIZATION FOR STANDARDIZATION. **ISO/IEC 27001:2022**: Information security, cybersecurity and privacy protection — Information security management systems. Genebra: ISO, 2022.

ZEITHAML, V. A.; BITNER, M. J.; GREMLER, D. D. **Services Marketing: Integrating Customer Focus Across the Firm**. 7. ed. New York: McGraw-Hill Education, 2018.

HUNKELER, U.; TRUONG, H. L.; STANFORD-CLARK, A. MQTT-S — A Publish/Subscribe Protocol for Wireless Sensor Networks. In: **3rd International Conference on Communication Systems Software and Middleware and Workshops (COMSWARE '08)**. Bangalore: IEEE, 2008. p. 791-798.

ORACLE CORPORATION. **Java 17 Language Features**. Redwood City: Oracle, 2021. Disponível em: https://openjdk.org/projects/jdk/17/. Acesso em: 10 jun. 2026.

PIVOTAL SOFTWARE. **Spring Boot Reference Documentation 3.3.1**. San Francisco: VMware, 2024. Disponível em: https://docs.spring.io/spring-boot/docs/3.3.1/reference/html/. Acesso em: 10 jun. 2026.

VERCEL INC. **Next.js 15 Documentation**. San Francisco: Vercel, 2024. Disponível em: https://nextjs.org/docs. Acesso em: 10 jun. 2026.

ECLIPSE FOUNDATION. **Mosquitto MQTT Broker Documentation**. Ottawa: Eclipse Foundation, 2023. Disponível em: https://mosquitto.org/documentation/. Acesso em: 10 jun. 2026.

NODE-RED PROJECT. **Node-RED Documentation**. OpenJS Foundation, 2024. Disponível em: https://nodered.org/docs/. Acesso em: 10 jun. 2026.

ESPRESSIF SYSTEMS. **ESP32 Technical Reference Manual**. Shanghai: Espressif Systems, 2023. Disponível em: https://www.espressif.com/sites/default/files/documentation/esp32_technical_reference_manual_en.pdf. Acesso em: 10 jun. 2026.

---

*Documento gerado automaticamente a partir da varredura completa do código-fonte do projeto Toyota Experience.*  
*Data de geração: 10 de junho de 2026.*  
*Versão: 1.0.0*
