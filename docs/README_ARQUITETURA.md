# Arquitetura de Integração e Monitoramento - Projeto Toyota**
## Este documento detalha a estrutura técnica da solução proposta para a automação e monitoramento da linha de produção (Chassi 04) na unidade de Sorocaba/SP.

**1. Integração Vertical (Chão de Fábrica → Nuvem)**
A integração vertical garante que os eventos físicos da fábrica se tornem dados estratégicos:
- Captação: Um Sensor Indutivo detecta a presença de peças metálicas (chassis) na linha.
- Controle: O sinal digital é processado por um PLC (CLP) / Gateway Industrial, que padroniza os dados.
- Nuvem: A informação é enviada via protocolo de comunicação para o Backend na AWS Cloud, onde os dados são processados e armazenados em tempo real.

**2. Integração Horizontal (Cadeia de Valor)**
A arquitetura conecta a produção com os demais pilares da empresa:
 - ERP (SAP/Oracle): Atualização automática de estoque e custos de produção conforme o sensor detecta a passagem das peças.
 - CRM (Salesforce): Permite que a área comercial acompanhe o status real do pedido do cliente final.

**3. Monitoramento e Gestão (MVP Técnico)**
Para atender aos requisitos de interface e visualização:
- Cálculo de OEE (Disponibilidade): O sistema monitora o tempo ativo do sensor vs. paradas não planejadas.
Atualmente, a arquitetura suporta um índice de 87.5% de disponibilidade.
- Interface Mobile (Toyota Smart Factory): Aplicação para supervisores que exibe o status operacional da linha, contagem de produção e alertas de integração com os sistemas corporativos.

**Tecnologias Envolvidas**
**Hardware:** Sensor Indutivo, PLC/Gateway.
**Cloud & Backend:** AWS Cloud Services.
**Sistemas Integrados:** SAP (ERP), Salesforce (CRM).
**Ferramentas de Design:** draw.io (Arquitetura e Mockup)

![Mapa de Fluxo Toyota](documentos/MAPA-DE-FLUXO.draw.io.png)
