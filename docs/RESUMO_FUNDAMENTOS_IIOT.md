# Resumo: Fundamentos de IIoT

## 1. Fundamentos e Conceitos Básicos de IoT e IIoT

### Diferenças entre IoT e IIoT

| Característica | IoT (Consumidor) | IIoT (Industrial) |
|----------------|------------------|-------------------|
| Foco | Ubiquidade e comodidade | Ambientes produtivos de missão crítica |
| Requisitos | Conectividade básica | Alta precisão, robustez, segurança cibernética e alta disponibilidade |

### Pilares da Estrutura

1. **Hardware** - Sensores e atuadores que coletam e executam ações
2. **Conectividade** - Redes que transportam os dados
3. **Inteligência/Processamento** - Cloud, Edge e On-device para análise e tomada de decisão

### Indústria 4.0 - Conceitos-Chave

- **Gêmeos Digitais (Digital Twins)**: Réplicas virtuais que simulam processos reais, permitindo testes e previsões sem impacto na produção
- **Manutenção Preditiva**: Uso de dados contínuos de sensores para prever falhas antes que ocorram, reduzindo paradas não planejadas

### Convergência TI/TO

| Área | Prioridade de Segurança |
|------|------------------------|
| TI (Tecnologia da Informação) | Confidencialidade |
| TO (Tecnologia Operacional) | Disponibilidade |

A integração requer equilíbrio entre essas prioridades conflitantes.

---

## 2. Hardware, Dispositivos e Sensores

### Características dos Dispositivos IIoT (Edge Devices)

- **Robustez**: Suportam condições hostis (vibrações, temperaturas extremas, poeira)
- **Operação contínua**: Funcionam com pouca manutenção
- **Energy Harvesting**: Coletam energia do ambiente (solar, vibração, calor) para operar

### Tipos de Sensores

| Tipo | Aplicação |
|------|-----------|
| Mecânicos/Vibração | MEMS e piezoelétricos para monitoramento de máquinas |
| Térmicos | Temperatura de equipamentos e processos |
| Acústicos | Identificação de vazamentos e anomalias sonoras |
| Pressão/Fluido | Monitoramento de tubulações e processos industriais |

### Arquitetura Física

**Gateways Industriais**:
- Atuam como conversores de protocolos
- Executam Edge Computing
- Conectam o chão de fábrica (sistemas legados) à nuvem

### Segurança em Hardware

- **Raiz de Confiança (Root of Trust)**: Base segura para autenticação e criptografia
- **Proibições**: Senhas padrão ou gravadas no código (hard-coded) são inaceitáveis
- **Requisito**: Autenticação robusta e atualizações seguras

---

## 3. Computação de Borda (Edge Computing)

### Problemas da Nuvem Centralizada

- Alta latência para decisões em tempo real
- Consumo excessivo de banda
- Custos elevados de transmissão e armazenamento
- Dependência total de conectividade

### Vantagens do Edge Computing

| Benefício | Descrição |
|-----------|-----------|
| **Baixa latência** | Ações em milissegundos, crítico para segurança |
| **Economia de banda** | Filtragem de ruídos antes do envio à nuvem |
| **Resiliência** | Operação autônoma mesmo sem conectividade |
| **Privacidade** | Dados sensíveis permanecem locais |

### Relação Edge-Cloud

| Camada | Função |
|--------|--------|
| **Edge (Borda)** | Ações táticas instantâneas, execução de modelos de IA |
| **Cloud (Nuvem)** | Processamento estratégico, armazenamento histórico (Big Data), treinamento de IA pesada |

**Fog Computing**: Camada intermediária entre Edge e Cloud.

---

## 4. Protocolos de Comunicação IIoT

### Comparativo de Protocolos

| Protocolo | Modelo | Característica Principal | Uso Ideal |
|-----------|--------|-------------------------|-----------|
| **MQTT** | Publish/Subscribe via Broker | Extremamente leve, desacoplado | Telemetria de sensores em redes limitadas |
| **XMPP** | Mensagens XML | Identidade, presença e estado dos dispositivos | Comunicação direta com confirmação de presença |
| **DDS** | Peer-to-Peer (sem broker) | Tempo real crítico, zero latência | Robótica, defesa, sistemas críticos |
| **AMQP** | Filas via Exchanges | Alta garantia de entrega, roteamento robusto | Integração corporativa |
| **OPC UA** | Cliente-Servidor | Semântica rica de dados | Automação, CLP/SCADA, redes locais |

### Resumo por Protocolo

**MQTT (Message Queuing Telemetry Transport)**
- Usa broker central
- Modelo publish/subscribe
- Ideal para dispositivos com recursos limitados

**XMPP (Extensible Messaging and Presence Protocol)**
- Baseado em XML
- Rastreia identidade e status online/offline
- Permite comandos diretos

**DDS (Data Distribution Service)**
- Arquitetura peer-to-peer
- Sem ponto único de falha (sem broker)
- Tempo real determinístico

**AMQP (Advanced Message Queuing Protocol)**
- Garantia de entrega com confirmações
- Roteamento flexível via exchanges
- Padrão corporativo para mensageria

**OPC UA (Open Platform Communications Unified Architecture)**
- Nativo da automação industrial
- Modelo de dados semântico rico
- Interoperabilidade com CLPs e SCADAs

---

## 5. Conectividade e Simulação de Redes

### Redes LPWAN (Low Power Wide Area Network)

| Tecnologia | Características |
|------------|-----------------|
| **LoRaWAN** | Longo alcance, baixo consumo, alta latência |
| **SigFox** | Ultra baixo consumo, poucos dados |
| **NB-IoT** | Usa infraestrutura celular existente |

**Indicado para**: Sensores remotos, monitoramento ambiental, leitura de medidores

### Redes Cabeadas e TSN

**Ethernet Industrial (PROFINET, EtherCAT)**:
- Alta velocidade e determinismo
- Suportada pelo padrão **TSN (Time-Sensitive Networking)**
- Essencial para controle de malha fechada

### 5G na Indústria

- **URLLC** (Ultra-Reliable Low-Latency Communication)
- Permite substituir cabos em robótica e AGVs
- Baixa latência e alta confiabilidade

### Retrofit

**Definição**: Modernização de máquinas antigas (legadas) com tecnologia atual

**Estratégia**:
- Acoplar sensores externos (ex: vibração)
- Usar módulos de baixo custo
- Manter equipamentos funcionais com nova capacidade de monitoramento

### Simuladores de Rede

| Simulador | Foco |
|-----------|------|
| **Cooja** | Contiki OS, ideal para IoT |
| **NS-3** | Matemático, alta escala, pesquisa acadêmica |
| **TOSSIM** | TinyOS, redes de sensores |

**Finalidade**: Validar infraestruturas sem custos físicos.

---

## 6. Plataformas IIoT e Stack MING

### Arquiteturas e Orquestração

**RAMI 4.0 (Reference Architecture Model Industrie 4.0)**:
- Modelo de referência para padronização
- Define camadas e níveis de integração

**Node-RED**:
- Interface low-code baseada em fluxos visuais
- Captura, transforma e orquestra lógicas de integração
- Conecta dispositivos, APIs e serviços

### Stack MING

Componentes fundamentais para laboratórios IIoT abertos:

| Componente | Função |
|------------|--------|
| **M - Mosquitto** | Broker MQTT que gerencia telemetria dos sensores |
| **I - InfluxDB** | Banco de dados de séries temporais para dados com timestamp |
| **N - Node-RED** | Motor de integração que conecta MQTT ao banco de dados |
| **G - Grafana** | Dashboards interativos com métricas, alertas e predições |

### Fluxo de Dados no Stack MING

```
Sensores → MQTT (Mosquitto) → Node-RED → InfluxDB → Grafana
```

**Mosquitto**: Recebe dados contínuos dos sensores via protocolo MQTT

**InfluxDB**: Armazena dados temporais de forma otimizada para consultas rápidas

**Node-RED**: Transforma, filtra e encaminha dados entre sistemas

**Grafana**: Visualiza dados em tempo real e históricos, gera alertas para operadores

---

## Conclusão

A IIoT combina hardware robusto, comunicação eficiente e processamento inteligente para transformar ambientes industriais. A escolha adequada de protocolos, arquiteturas (Edge vs Cloud) e plataformas (Stack MING) depende dos requisitos específicos de cada aplicação, sempre priorizando segurança, disponibilidade e baixa latência para operações críticas.
