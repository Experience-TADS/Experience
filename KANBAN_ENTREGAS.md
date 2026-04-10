# Quadro Kanban - Toyota Experience v2
## Projeto Integrador Interdisciplinar III

---

## 📦 ENTREGAS CONCLUÍDAS (DONE)

### 🎯 US-001: Estrutura Base do Backend
**Como** desenvolvedor  
**Quero** ter a estrutura base do projeto Spring Boot configurada  
**Para que** possa desenvolver as funcionalidades do sistema

**Critérios de Aceite:**
- [x] Spring Boot 3.3.1 configurado com Java 17
- [x] PostgreSQL como banco de dados principal
- [x] Spring Data JPA + Hibernate configurados
- [x] Maven como gerenciador de dependências
- [x] Lombok integrado para redução de boilerplate

**Evidências:** `pom.xml`, `application.properties`

---

### 🎯 US-002: Sistema de Autenticação JWT
**Como** usuário do sistema  
**Quero** fazer login com email e senha  
**Para que** possa acessar o sistema de forma segura

**Critérios de Aceite:**
- [x] Endpoint POST /api/usuario/login implementado
- [x] Geração de token JWT após autenticação bem-sucedida
- [x] Spring Security configurado
- [x] CustomUserDetailsService implementado
- [x] JwtAuthFilter para validação de tokens

**Evidências:** `security/`, `UsuarioController.java`, `LoginRequest.java`

---

### 🎯 US-003: CRUD de Usuários (PF e PJ)
**Como** administrador  
**Quero** gerenciar usuários pessoa física e jurídica  
**Para que** possa cadastrar clientes no sistema

**Critérios de Aceite:**
- [x] Entidade Usuario com herança JOINED
- [x] PessoaFisica com validação de CPF
- [x] PessoaJuridica com validação de CNPJ
- [x] Endpoints CRUD para ambos os tipos
- [x] Repositories implementados

**Evidências:** `entities/Usuario.java`, `entities/PessoaFisica.java`, `entities/PessoaJuridica.java`, `controllers/`

---

### 🎯 US-004: Gestão de Endereços
**Como** usuário  
**Quero** cadastrar meus endereços  
**Para que** possa receber comunicações e entregas

**Critérios de Aceite:**
- [x] Entidade Endereco criada
- [x] CRUD completo implementado
- [x] EnderecoController com endpoints REST
- [x] EnderecoService com lógica de negócio

**Evidências:** `entities/Endereco.java`, `controllers/EnderecoController.java`

---

### 🎯 US-005: Gestão de Telefones
**Como** usuário  
**Quero** cadastrar meus telefones de contato  
**Para que** possa ser contatado pela concessionária

**Critérios de Aceite:**
- [x] Entidade Telefone criada
- [x] CRUD completo implementado
- [x] TelefoneController com endpoints REST
- [x] TelefoneService com lógica de negócio

**Evidências:** `entities/Telefone.java`, `controllers/TelefoneController.java`

---

### 🎯 US-006: Estrutura Básica de Pedidos
**Como** sistema  
**Quero** ter uma estrutura básica para registrar pedidos  
**Para que** possa evoluir para o acompanhamento de fabricação

**Critérios de Aceite:**
- [x] Entidade Pedido criada
- [x] Campos básicos: id, idCliente, idVendedor, dataPedido, valorTotal
- [x] PedidoController com GET e POST
- [x] PedidoRepository implementado

**Evidências:** `entities/Pedido.java`, `controllers/PedidoController.java`

---

### 🎯 US-007: Documentação de Arquitetura
**Como** equipe de desenvolvimento  
**Quero** ter a arquitetura do sistema documentada  
**Para que** possamos seguir um padrão consistente

**Critérios de Aceite:**
- [x] Relatório de arquitetura completo
- [x] Diagnóstico da v1 documentado
- [x] Proposta de arquitetura hexagonal (v2)
- [x] Diagrama de classes UML
- [x] Identificação de problemas críticos

**Evidências:** `documentacao/RELATORIO_ARQUITETURA.md`, `data/UML.md`

---

## 🚧 ENTREGAS PENDENTES - ABRIL 2026

### 🎯 US-008: Refatoração para Arquitetura Hexagonal
**Como** desenvolvedor  
**Quero** refatorar o código para arquitetura hexagonal  
**Para que** o sistema tenha baixo acoplamento e alta coesão

**Critérios de Aceite:**
- [ ] Criar estrutura de pacotes: domain/, application/, infrastructure/, adapter/
- [ ] Criar objetos de domínio puros (sem anotações JPA)
- [ ] Definir interfaces de porta (in e out)
- [ ] Implementar adapters JPA separados das entidades de domínio
- [ ] Criar mappers domínio ↔ entidade ↔ DTO

**Prioridade:** ALTA  
**Estimativa:** 5 dias  
**Dependências:** Nenhuma

---

### 🎯 US-009: Entidade Veículo e Status de Fabricação
**Como** cliente  
**Quero** visualizar o status de fabricação do meu veículo  
**Para que** possa acompanhar o andamento da produção

**Critérios de Aceite:**
- [ ] Criar entidade Veiculo (modelo, marca, cor, preço, imagemUrl)
- [ ] Criar enum StatusFabricacao (AGUARDANDO, EM_FABRICACAO, PINTURA, CONTROLE_QUALIDADE, CONCLUIDO, ENTREGUE)
- [ ] Criar entidade StatusHistorico
- [ ] Adicionar relacionamento Pedido → Veiculo
- [ ] Adicionar campo status em Pedido
- [ ] Implementar máquina de estados para transições

**Prioridade:** CRÍTICA  
**Estimativa:** 3 dias  
**Dependências:** US-008

---

### 🎯 US-010: Integração MQTT - Configuração
**Como** sistema  
**Quero** receber eventos de fabricação via MQTT  
**Para que** possa atualizar o status dos pedidos em tempo real

**Critérios de Aceite:**
- [ ] Adicionar dependências spring-integration-mqtt
- [ ] Criar MqttConfig com ConnectionFactory e MessageChannel
- [ ] Configurar broker Mosquitto local (porta 1883)
- [ ] Definir estrutura de tópicos: experience/fabricacao/{pedidoId}/status
- [ ] Configurar QoS 1 para status, QoS 0 para sensores
- [ ] Implementar autenticação no broker

**Prioridade:** CRÍTICA  
**Estimativa:** 2 dias  
**Dependências:** US-009

---

### 🎯 US-011: Adapter MQTT - Recepção de Status
**Como** sistema  
**Quero** processar eventos de mudança de status via MQTT  
**Para que** o histórico de fabricação seja atualizado automaticamente

**Critérios de Aceite:**
- [ ] Criar MqttFabricacaoAdapter com @ServiceActivator
- [ ] Implementar FabricacaoUseCase
- [ ] Validar transições de status (máquina de estados)
- [ ] Persistir StatusHistorico a cada mudança
- [ ] Atualizar status atual do Pedido
- [ ] Tratar payload JSON do ESP32

**Prioridade:** CRÍTICA  
**Estimativa:** 3 dias  
**Dependências:** US-010

---

### 🎯 US-012: Adapter MQTT - Leituras de Sensores
**Como** operador  
**Quero** visualizar dados de sensores da linha de produção  
**Para que** possa monitorar temperatura e vibração

**Critérios de Aceite:**
- [ ] Criar entidade LeituraSensor (deviceId, temperatura, vibracao, timestamp)
- [ ] Criar MqttSensorAdapter com @ServiceActivator
- [ ] Assinar tópico experience/fabricacao/{pedidoId}/sensor
- [ ] Persistir leituras brutas no banco
- [ ] Implementar endpoint GET /api/dados
- [ ] Implementar endpoint GET /api/dados/{pedidoId}

**Prioridade:** ALTA  
**Estimativa:** 2 dias  
**Dependências:** US-011

---

### 🎯 US-013: Sistema de Alertas IoT
**Como** operador  
**Quero** receber alertas de anomalias nos sensores  
**Para que** possa agir rapidamente em caso de problemas

**Critérios de Aceite:**
- [ ] Criar AlertaService com regras de anomalia
- [ ] Detectar temperatura > 80°C ou vibração > 2.0
- [ ] Criar entidade Alerta
- [ ] Implementar endpoint GET /api/alertas
- [ ] Filtrar alertas por pedido e período
- [ ] Adicionar paginação

**Prioridade:** MÉDIA  
**Estimativa:** 2 dias  
**Dependências:** US-012

---

### 🎯 US-014: Módulo de Notícias
**Como** administrador  
**Quero** publicar notícias da marca  
**Para que** os clientes vejam conteúdo no dashboard

**Critérios de Aceite:**
- [ ] Criar entidade Noticia (titulo, resumo, conteudo, imagemUrl, publicadoEm, ativo)
- [ ] Implementar CRUD admin (POST/PUT/DELETE /api/noticias)
- [ ] Implementar listagem pública paginada (GET /api/noticias)
- [ ] Proteger endpoints admin com ROLE_ADMIN
- [ ] Ordenar por data de publicação (mais recentes primeiro)

**Prioridade:** MÉDIA  
**Estimativa:** 2 dias  
**Dependências:** US-008

---

### 🎯 US-015: Dashboard Agregado
**Como** cliente  
**Quero** visualizar meu pedido e notícias em uma única tela  
**Para que** tenha uma visão completa do sistema

**Critérios de Aceite:**
- [ ] Criar DashboardController
- [ ] Implementar endpoint GET /api/dashboard
- [ ] Retornar status atual do pedido do cliente autenticado
- [ ] Retornar últimas 5 notícias ativas
- [ ] Criar DashboardResponse DTO
- [ ] Proteger com JWT

**Prioridade:** ALTA  
**Estimativa:** 1 dia  
**Dependências:** US-009, US-014

---

### 🎯 US-016: Sistema de Roles e Autorização
**Como** administrador  
**Quero** controlar acesso por perfil de usuário  
**Para que** cada tipo de usuário veja apenas o que é permitido

**Critérios de Aceite:**
- [ ] Adicionar campo role em Usuario (ROLE_CLIENTE, ROLE_VENDEDOR, ROLE_ADMIN, ROLE_IOT)
- [ ] Refatorar SecurityConfig para proteger rotas por role
- [ ] Clientes: acesso apenas aos próprios pedidos
- [ ] Vendedores: acesso a todos os pedidos
- [ ] Admin: acesso total + CRUD de notícias
- [ ] IoT: apenas POST em endpoints de fabricação

**Prioridade:** CRÍTICA  
**Estimativa:** 2 dias  
**Dependências:** US-008

---

### 🎯 US-017: DTOs de Request e Response
**Como** desenvolvedor  
**Quero** separar entidades JPA dos contratos de API  
**Para que** mudanças no banco não quebrem a API

**Critérios de Aceite:**
- [ ] Criar DTOs de request para todos os endpoints POST/PUT
- [ ] Criar DTOs de response para todos os endpoints GET
- [ ] Nunca expor senhaHash nas respostas
- [ ] Implementar mappers (manual ou MapStruct)
- [ ] Remover entidades JPA dos controllers

**Prioridade:** ALTA  
**Estimativa:** 3 dias  
**Dependências:** US-008

---

### 🎯 US-018: Correção de Relacionamentos JPA
**Como** desenvolvedor  
**Quero** ter relacionamentos JPA corretos  
**Para que** o banco mantenha integridade referencial

**Critérios de Aceite:**
- [ ] Adicionar @ManyToOne em Pedido → Cliente
- [ ] Adicionar @ManyToOne em Pedido → Vendedor
- [ ] Adicionar @ManyToOne em Endereco → Usuario
- [ ] Adicionar @ManyToOne em Telefone → Usuario
- [ ] Adicionar @OneToMany em Pedido → StatusHistorico
- [ ] Configurar cascade e fetch adequados

**Prioridade:** ALTA  
**Estimativa:** 2 dias  
**Dependências:** US-008

---

### 🎯 US-019: Tratamento Global de Erros
**Como** desenvolvedor  
**Quero** ter tratamento consistente de exceções  
**Para que** a API retorne erros padronizados

**Critérios de Aceite:**
- [ ] Criar @ControllerAdvice global
- [ ] Criar exceções customizadas (ResourceNotFoundException, ValidationException)
- [ ] Retornar ErrorResponse padronizado (timestamp, status, message, path)
- [ ] Tratar exceções de validação (400)
- [ ] Tratar recursos não encontrados (404)
- [ ] Tratar erros de autenticação (401) e autorização (403)

**Prioridade:** MÉDIA  
**Estimativa:** 1 dia  
**Dependências:** US-017

---

### 🎯 US-020: Paginação de Listagens
**Como** usuário  
**Quero** que as listagens sejam paginadas  
**Para que** o sistema tenha boa performance com muitos dados

**Critérios de Aceite:**
- [ ] Adicionar Pageable em todos os métodos findAll()
- [ ] Retornar Page<T> em vez de List<T>
- [ ] Aceitar parâmetros page, size, sort via query string
- [ ] Incluir metadados de paginação na resposta (totalPages, totalElements)
- [ ] Aplicar em: pedidos, usuários, notícias, dados de sensores

**Prioridade:** MÉDIA  
**Estimativa:** 1 dia  
**Dependências:** US-017

---

## 🔍 REVISÕES DE CÓDIGO NECESSÁRIAS

### 🔒 REV-001: Segurança - Credenciais Expostas
**Problema:** Credenciais de email Gmail e chave JWT hardcoded em `application.properties`

**Ações Necessárias:**
- [ ] Remover credenciais do application.properties
- [ ] Criar arquivo .env.example com variáveis de ambiente
- [ ] Configurar variáveis de ambiente no sistema
- [ ] Atualizar application.properties para usar ${ENV_VAR}
- [ ] Adicionar .env ao .gitignore
- [ ] Documentar variáveis necessárias no README

**Prioridade:** CRÍTICA  
**Risco:** Segurança  
**Estimativa:** 1 hora

---

### 🔒 REV-002: Segurança - Rotas Desprotegidas
**Problema:** SecurityConfig com `.requestMatchers("/**").permitAll()` torna JWT ineficaz

**Ações Necessárias:**
- [ ] Remover permitAll() global
- [ ] Definir rotas públicas explicitamente (/api/usuario/login, /api/noticias)
- [ ] Proteger rotas de cliente com hasRole("CLIENTE")
- [ ] Proteger rotas de vendedor com hasRole("VENDEDOR")
- [ ] Proteger rotas admin com hasRole("ADMIN")
- [ ] Testar acesso não autorizado retorna 403

**Prioridade:** CRÍTICA  
**Risco:** Segurança  
**Estimativa:** 2 horas

---

### 🔧 REV-003: Arquitetura - Dependência JWT Duplicada
**Problema:** Dois providers JWT no pom.xml (jjwt 0.11.5 e auth0 4.5.0), apenas jjwt é usado

**Ações Necessárias:**
- [ ] Remover dependência com.auth0:java-jwt do pom.xml
- [ ] Verificar se nenhum import de auth0 existe no código
- [ ] Executar mvn clean install para validar
- [ ] Atualizar documentação de dependências

**Prioridade:** MÉDIA  
**Risco:** Manutenibilidade  
**Estimativa:** 15 minutos

---

### 🔧 REV-004: Arquitetura - JwtUtil com Estado Estático
**Problema:** Chave JWT em campo static quebra injeção de dependência do Spring

**Ações Necessárias:**
- [ ] Remover modificador static da chave JWT
- [ ] Injetar chave via @Value("${jwt.secret}")
- [ ] Tornar JwtUtil um @Component sem estado estático
- [ ] Atualizar testes unitários para injetar JwtUtil
- [ ] Validar que Spring gerencia o ciclo de vida

**Prioridade:** ALTA  
**Risco:** Testabilidade  
**Estimativa:** 1 hora

---

### 🔧 REV-005: Arquitetura - Entidades Expostas na API
**Problema:** Controllers retornam @Entity diretamente, acoplando API ao modelo de dados

**Ações Necessárias:**
- [ ] Criar DTOs de response para todos os endpoints GET
- [ ] Nunca retornar entidades JPA diretamente
- [ ] Implementar mappers para conversão
- [ ] Validar que senhaHash nunca é exposto
- [ ] Atualizar todos os controllers

**Prioridade:** ALTA  
**Risco:** Segurança + Manutenibilidade  
**Estimativa:** 4 horas  
**Relacionado:** US-017

---

### 🔧 REV-006: Arquitetura - Relacionamentos JPA Ausentes
**Problema:** Pedido usa int primitivo para idCliente/idVendedor, sem @ManyToOne

**Ações Necessárias:**
- [ ] Substituir int idCliente por @ManyToOne Cliente cliente
- [ ] Substituir int idVendedor por @ManyToOne Usuario vendedor
- [ ] Adicionar FK em Endereco → Usuario
- [ ] Adicionar FK em Telefone → Usuario
- [ ] Criar migrations Flyway para alterar schema
- [ ] Atualizar queries e services

**Prioridade:** ALTA  
**Risco:** Integridade de Dados  
**Estimativa:** 3 horas  
**Relacionado:** US-018

---

### 🔧 REV-007: Qualidade - Services Retornam Null
**Problema:** Services retornam null quando recurso não encontrado, em vez de lançar exceção

**Ações Necessárias:**
- [ ] Criar ResourceNotFoundException
- [ ] Substituir return null por throw new ResourceNotFoundException()
- [ ] Atualizar todos os services (Usuario, Pedido, Endereco, Telefone)
- [ ] Adicionar @ControllerAdvice para capturar exceção
- [ ] Retornar 404 com mensagem clara

**Prioridade:** MÉDIA  
**Risco:** Experiência do Usuário  
**Estimativa:** 2 horas  
**Relacionado:** US-019

---

### 🔧 REV-008: Qualidade - Injeção de Dependência Inconsistente
**Problema:** Alguns controllers usam @Autowired (field injection), outros usam construtor

**Ações Necessárias:**
- [ ] Padronizar para constructor injection em todos os controllers
- [ ] Remover @Autowired de campos
- [ ] Adicionar final nos campos injetados
- [ ] Validar que Lombok @RequiredArgsConstructor funciona
- [ ] Atualizar guia de estilo no README

**Prioridade:** BAIXA  
**Risco:** Consistência  
**Estimativa:** 30 minutos

---

### 🔧 REV-009: Funcional - Pedido Sem Status de Fabricação
**Problema:** Entidade Pedido não possui campos de status nem histórico de transições

**Ações Necessárias:**
- [ ] Adicionar campo StatusFabricacao status em Pedido
- [ ] Criar entidade StatusHistorico
- [ ] Adicionar @OneToMany Pedido → StatusHistorico
- [ ] Implementar lógica de transição de status
- [ ] Validar máquina de estados
- [ ] Criar migration Flyway

**Prioridade:** CRÍTICA  
**Risco:** Funcionalidade Principal  
**Estimativa:** 3 horas  
**Relacionado:** US-009

---

### 🔧 REV-010: Funcional - Herança Usuario Sem Role
**Problema:** Usuario não possui campo de perfil, impossível diferenciar cliente de vendedor

**Ações Necessárias:**
- [ ] Adicionar campo String role em Usuario
- [ ] Criar enum UserRole (CLIENTE, VENDEDOR, ADMIN, IOT)
- [ ] Atualizar CustomUserDetailsService para usar role real
- [ ] Remover hardcoded "USER"
- [ ] Criar migration para adicionar coluna
- [ ] Atualizar cadastro para definir role

**Prioridade:** CRÍTICA  
**Risco:** Autorização  
**Estimativa:** 2 horas  
**Relacionado:** US-016

---

### 📊 REV-011: Performance - Ausência de Paginação
**Problema:** Todos os findAll() retornam listas completas, inviável em produção

**Ações Necessárias:**
- [ ] Adicionar Pageable em todos os repositories
- [ ] Atualizar services para aceitar Pageable
- [ ] Atualizar controllers para retornar Page<T>
- [ ] Definir tamanho padrão de página (20 itens)
- [ ] Adicionar índices no banco para campos de ordenação
- [ ] Documentar parâmetros de paginação

**Prioridade:** MÉDIA  
**Risco:** Performance  
**Estimativa:** 2 horas  
**Relacionado:** US-020

---

### 📋 REV-012: Documentação - API Sem Especificação OpenAPI
**Problema:** Endpoints não possuem documentação formal (Swagger/OpenAPI)

**Ações Necessárias:**
- [ ] Adicionar dependência springdoc-openapi-starter-webmvc-ui
- [ ] Configurar Swagger UI em /swagger-ui.html
- [ ] Adicionar @Operation em todos os endpoints
- [ ] Adicionar @Schema nos DTOs
- [ ] Documentar códigos de resposta (200, 400, 401, 404)
- [ ] Adicionar exemplos de request/response

**Prioridade:** MÉDIA  
**Risco:** Documentação  
**Estimativa:** 3 horas

---

## 📊 MÉTRICAS DO PROJETO

### Entregas Concluídas
- **Total:** 7 histórias de usuário
- **Pontos de Função:** Backend base, autenticação, CRUD completo

### Entregas Pendentes (Abril 2026)
- **Total:** 13 histórias de usuário
- **Prioridade Crítica:** 4 (US-009, US-010, US-011, US-016)
- **Prioridade Alta:** 5 (US-008, US-012, US-015, US-017, US-018)
- **Prioridade Média:** 4 (US-013, US-014, US-019, US-020)

### Revisões de Código
- **Total:** 12 revisões identificadas
- **Prioridade Crítica:** 4 (REV-001, REV-002, REV-009, REV-010)
- **Prioridade Alta:** 3 (REV-004, REV-005, REV-006)
- **Prioridade Média:** 4 (REV-003, REV-007, REV-011, REV-012)
- **Prioridade Baixa:** 1 (REV-008)

### Estimativa Total (Abril 2026)
- **Desenvolvimento:** ~30 dias de trabalho
- **Revisões:** ~20 horas de trabalho
- **Risco Principal:** Integração MQTT (US-010, US-011, US-012)

---

## 🎯 ROADMAP SUGERIDO - ABRIL 2026

### Semana 1 (01-07 Abril)
1. REV-001: Remover credenciais expostas (CRÍTICO)
2. REV-002: Proteger rotas (CRÍTICO)
3. REV-010: Adicionar roles em Usuario (CRÍTICO)
4. US-008: Iniciar refatoração hexagonal

### Semana 2 (08-14 Abril)
1. US-008: Concluir refatoração hexagonal
2. US-009: Implementar Veiculo e StatusFabricacao (CRÍTICO)
3. REV-009: Adicionar status em Pedido (CRÍTICO)
4. US-017: Criar DTOs de request/response

### Semana 3 (15-21 Abril)
1. US-010: Configurar integração MQTT (CRÍTICO)
2. US-011: Implementar adapter de status MQTT (CRÍTICO)
3. US-012: Implementar adapter de sensores
4. US-016: Implementar autorização por roles

### Semana 4 (22-30 Abril)
1. US-013: Sistema de alertas IoT
2. US-014: Módulo de notícias
3. US-015: Dashboard agregado
4. US-018: Corrigir relacionamentos JPA
5. US-019: Tratamento global de erros
6. US-020: Paginação de listagens
7. Revisões restantes (REV-003 a REV-012)

---

## 📝 NOTAS IMPORTANTES

### Conformidade com Requisitos da UC
- **Cobertura Atual:** 91% (39% conforme + 52% parcial)
- **Lacunas Principais:**
  - Camada Cloud (deploy e infraestrutura)
  - Interface Industrial (KPIs e alertas operacionais)
  - Diagramas formais (ER, camadas, OpenAPI)

### Dependências Externas
- **Mosquitto MQTT Broker:** Deve ser instalado e configurado localmente
- **ESP32:** Simulador de linha de produção (hardware externo)
- **PostgreSQL:** Já configurado, mas precisa de migrations Flyway

### Riscos Identificados
1. **Integração MQTT:** Primeira vez que a equipe trabalha com MQTT no Spring
2. **Refatoração Hexagonal:** Mudança arquitetural significativa
3. **Credenciais Expostas:** Risco de segurança ativo no repositório

---

**Última Atualização:** 10 de Abril de 2026  
**Responsável:** Equipe Toyota Experience  
**Próxima Revisão:** 17 de Abril de 2026
