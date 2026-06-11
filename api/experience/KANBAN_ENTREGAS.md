# Quadro Kanban - Toyota Experience v2
## Projeto Integrador Interdisciplinar III
**Última Atualização:** 10 de Abril de 2026

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

### 🎯 US-008: Entidade Veículo
**Como** sistema  
**Quero** ter uma entidade Veículo para representar os carros fabricados  
**Para que** possa associar veículos aos pedidos

**Critérios de Aceite:**
- [x] Entidade Veiculo criada (id, idProduto, chassi, StatusVeiculo)
- [x] VeiculoController com CRUD completo
- [x] VeiculoService implementado
- [x] VeiculoRepository criado
- [x] Endpoints REST funcionais

**Evidências:** `entities/Veiculo.java`, `controllers/VeiculoController.java`, `services/VeiculoService.java`

---

### 🎯 US-009: Entidade Produto
**Como** vendedor  
**Quero** cadastrar produtos (modelos de veículos)  
**Para que** possa criar pedidos com produtos específicos

**Critérios de Aceite:**
- [x] Entidade Produto criada (idProduto, modelo, cor, versao, ano)
- [x] ProdutoController com CRUD completo
- [x] ProdutoService implementado
- [x] ProdutoRepository criado
- [x] Endpoints REST em /api/produto

**Evidências:** `entities/Produto.java`, `controllers/ProdutoController.java`, `services/ProdutoService.java`

---

### 🎯 US-010: Entidade ItemPedido
**Como** sistema  
**Quero** ter itens de pedido para relacionar produtos aos pedidos  
**Para que** um pedido possa conter múltiplos produtos

**Critérios de Aceite:**
- [x] Entidade ItemPedido criada (idItemPedido, produto, quantidade)
- [x] Relacionamento @ManyToOne com Produto
- [x] ItemPedidoController com CRUD completo
- [x] ItemPedidoService implementado
- [x] Endpoints REST em /itens-pedido

**Evidências:** `entities/ItemPedido.java`, `controllers/ItemPedidoController.java`, `services/ItemPedidoService.java`

---

### 🎯 US-011: Configuração MQTT Preparada
**Como** desenvolvedor  
**Quero** ter as configurações MQTT no application.properties  
**Para que** o sistema esteja pronto para integração IoT

**Critérios de Aceite:**
- [x] Configuração mqtt.broker.url definida (tcp://localhost:1883)
- [x] Configuração mqtt.client.id definida (backend-experience)
- [x] Tópicos MQTT definidos (status e sensor)
- [x] Estrutura de tópicos documentada

**Evidências:** `application.properties` (linhas 20-24)

---

## 🚧 ENTREGAS PENDENTES - ABRIL 2026

## 🚧 ENTREGAS PENDENTES - ABRIL 2026

### 🎯 US-012: Enum StatusFabricacao e Máquina de Estados
**Como** cliente  
**Quero** visualizar o status de fabricação do meu veículo  
**Para que** possa acompanhar o andamento da produção

**Critérios de Aceite:**
- [ ] Criar enum StatusFabricacao (AGUARDANDO, EM_FABRICACAO, PINTURA, CONTROLE_QUALIDADE, CONCLUIDO, ENTREGUE, CANCELADO)
- [ ] Adicionar campo status em Veiculo (substituir String por enum)
- [ ] Implementar máquina de estados para validar transições
- [ ] Criar entidade StatusHistorico para rastrear mudanças
- [ ] Adicionar relacionamento @OneToMany Veiculo → StatusHistorico

**Prioridade:** CRÍTICA  
**Estimativa:** 2 dias  
**Dependências:** Nenhuma

---

### 🎯 US-013: Relacionamento Pedido → ItemPedido
**Como** sistema  
**Quero** ter relacionamento bidirecional entre Pedido e ItemPedido  
**Para que** possa consultar os itens de um pedido

**Critérios de Aceite:**
- [ ] Descomentar @ManyToOne em ItemPedido → Pedido
- [ ] Adicionar @OneToMany em Pedido → List<ItemPedido>
- [ ] Configurar cascade adequado (CascadeType.ALL)
- [ ] Configurar fetch (FetchType.LAZY)
- [ ] Testar criação de pedido com itens

**Prioridade:** ALTA  
**Estimativa:** 1 dia  
**Dependências:** Nenhuma

---

### 🎯 US-014: Relacionamentos JPA em Pedido
**Como** desenvolvedor  
**Quero** ter relacionamentos JPA corretos em Pedido  
**Para que** o banco mantenha integridade referencial

**Critérios de Aceite:**
- [ ] Substituir int idCliente por @ManyToOne Cliente cliente
- [ ] Substituir int idVendedor por @ManyToOne Usuario vendedor
- [ ] Adicionar @ManyToOne em Endereco → Usuario
- [ ] Adicionar @ManyToOne em Telefone → Usuario
- [ ] Criar migrations para alterar schema
- [ ] Atualizar services e controllers

**Prioridade:** ALTA  
**Estimativa:** 3 dias  
**Dependências:** Nenhuma

---

### 🎯 US-015: Relacionamento Veiculo → Produto
**Como** sistema  
**Quero** relacionar Veiculo com Produto  
**Para que** cada veículo tenha suas especificações

**Critérios de Aceite:**
- [ ] Substituir int idProduto por @ManyToOne Produto produto
- [ ] Remover campo idProduto primitivo
- [ ] Atualizar VeiculoService para trabalhar com objeto Produto
- [ ] Atualizar VeiculoController
- [ ] Testar criação de veículo com produto

**Prioridade:** ALTA  
**Estimativa:** 1 dia  
**Dependências:** Nenhuma

---

### 🎯 US-016: Integração MQTT - Dependências e Config
**Como** sistema  
**Quero** receber eventos de fabricação via MQTT  
**Para que** possa atualizar o status dos veículos em tempo real

**Critérios de Aceite:**
- [ ] Adicionar dependências spring-integration-mqtt no pom.xml
- [ ] Adicionar spring-boot-starter-integration
- [ ] Criar classe MqttConfig com @Configuration
- [ ] Configurar ConnectionFactory para broker Mosquitto
- [ ] Configurar MessageChannel para receber mensagens
- [ ] Definir QoS 1 para status, QoS 0 para sensores

**Prioridade:** CRÍTICA  
**Estimativa:** 2 dias  
**Dependências:** US-012

---

### 🎯 US-017: Adapter MQTT - Recepção de Status
**Como** sistema  
**Quero** processar eventos de mudança de status via MQTT  
**Para que** o histórico de fabricação seja atualizado automaticamente

**Critérios de Aceite:**
- [ ] Criar MqttFabricacaoAdapter com @ServiceActivator
- [ ] Assinar tópico experience/fabricacao/+/status
- [ ] Implementar FabricacaoService com lógica de negócio
- [ ] Validar transições de status (máquina de estados)
- [ ] Persistir StatusHistorico a cada mudança
- [ ] Atualizar status atual do Veiculo
- [ ] Tratar payload JSON do ESP32

**Prioridade:** CRÍTICA  
**Estimativa:** 3 dias  
**Dependências:** US-016

---

### 🎯 US-018: Adapter MQTT - Leituras de Sensores
**Como** operador  
**Quero** visualizar dados de sensores da linha de produção  
**Para que** possa monitorar temperatura e vibração

**Critérios de Aceite:**
- [ ] Criar entidade LeituraSensor (deviceId, temperatura, vibracao, timestamp)
- [ ] Criar MqttSensorAdapter com @ServiceActivator
- [ ] Assinar tópico experience/fabricacao/+/sensor
- [ ] Persistir leituras brutas no banco
- [ ] Criar DadosController com endpoint GET /api/dados
- [ ] Implementar GET /api/dados/{veiculoId}

**Prioridade:** ALTA  
**Estimativa:** 2 dias  
**Dependências:** US-017

---

### 🎯 US-019: Sistema de Alertas IoT
**Como** operador  
**Quero** receber alertas de anomalias nos sensores  
**Para que** possa agir rapidamente em caso de problemas

**Critérios de Aceite:**
- [ ] Criar entidade Alerta (tipo, mensagem, severidade, timestamp)
- [ ] Criar AlertaService com regras de anomalia
- [ ] Detectar temperatura > 80°C ou vibração > 2.0
- [ ] Implementar endpoint GET /api/alertas
- [ ] Filtrar alertas por veículo e período
- [ ] Adicionar paginação

**Prioridade:** MÉDIA  
**Estimativa:** 2 dias  
**Dependências:** US-018

---

### 🎯 US-020: Módulo de Notícias
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
**Dependências:** US-024 (roles)

---

### 🎯 US-021: Dashboard Agregado
**Como** cliente  
**Quero** visualizar meu pedido e notícias em uma única tela  
**Para que** tenha uma visão completa do sistema

**Critérios de Aceite:**
- [ ] Criar DashboardController
- [ ] Implementar endpoint GET /api/dashboard
- [ ] Retornar status atual do veículo do cliente autenticado
- [ ] Retornar últimas 5 notícias ativas
- [ ] Criar DashboardResponse DTO
- [ ] Proteger com JWT

**Prioridade:** ALTA  
**Estimativa:** 1 dia  
**Dependências:** US-012, US-020

---

### 🎯 US-022: DTOs de Request e Response
**Como** desenvolvedor  
**Quero** separar entidades JPA dos contratos de API  
**Para que** mudanças no banco não quebrem a API

**Critérios de Aceite:**
- [ ] Criar DTOs de request para todos os endpoints POST/PUT
- [ ] Criar DTOs de response para todos os endpoints GET
- [ ] Nunca expor senhaHash nas respostas
- [ ] Implementar mappers (manual ou MapStruct)
- [ ] Remover entidades JPA dos controllers
- [ ] Aplicar em: Usuario, Pedido, Veiculo, Produto, ItemPedido

**Prioridade:** ALTA  
**Estimativa:** 4 dias  
**Dependências:** Nenhuma

---

### 🎯 US-023: Tratamento Global de Erros
**Como** desenvolvedor  
**Quero** ter tratamento consistente de exceções  
**Para que** a API retorne erros padronizados

**Critérios de Aceite:**
- [ ] Criar @ControllerAdvice global
- [ ] Criar exceções customizadas (ResourceNotFoundException, ValidationException)
- [ ] Criar ErrorResponse DTO (timestamp, status, message, path)
- [ ] Tratar exceções de validação (400)
- [ ] Tratar recursos não encontrados (404)
- [ ] Tratar erros de autenticação (401) e autorização (403)

**Prioridade:** ALTA  
**Estimativa:** 1 dia  
**Dependências:** US-022

---

### 🎯 US-024: Sistema de Roles e Autorização
**Como** administrador  
**Quero** controlar acesso por perfil de usuário  
**Para que** cada tipo de usuário veja apenas o que é permitido

**Critérios de Aceite:**
- [ ] Adicionar campo role em Usuario (enum UserRole)
- [ ] Criar enum UserRole (CLIENTE, VENDEDOR, ADMIN, IOT)
- [ ] Refatorar SecurityConfig para proteger rotas por role
- [ ] Clientes: acesso apenas aos próprios pedidos
- [ ] Vendedores: acesso a todos os pedidos
- [ ] Admin: acesso total + CRUD de notícias
- [ ] IoT: apenas POST em endpoints de fabricação

**Prioridade:** CRÍTICA  
**Estimativa:** 2 dias  
**Dependências:** Nenhuma

---

### 🎯 US-025: Paginação de Listagens
**Como** usuário  
**Quero** que as listagens sejam paginadas  
**Para que** o sistema tenha boa performance com muitos dados

**Critérios de Aceite:**
- [ ] Adicionar Pageable em todos os métodos findAll()
- [ ] Retornar Page<T> em vez de List<T>
- [ ] Aceitar parâmetros page, size, sort via query string
- [ ] Incluir metadados de paginação na resposta
- [ ] Aplicar em: pedidos, usuários, veículos, produtos, notícias, dados de sensores

**Prioridade:** MÉDIA  
**Estimativa:** 2 dias  
**Dependências:** US-022

---

### 🎯 US-026: Refatoração para Arquitetura Hexagonal
**Como** desenvolvedor  
**Quero** refatorar o código para arquitetura hexagonal  
**Para que** o sistema tenha baixo acoplamento e alta coesão

**Critérios de Aceite:**
- [ ] Criar estrutura de pacotes: domain/, application/, infrastructure/, adapter/
- [ ] Criar objetos de domínio puros (sem anotações JPA)
- [ ] Definir interfaces de porta (in e out)
- [ ] Implementar adapters JPA separados das entidades de domínio
- [ ] Criar mappers domínio ↔ entidade ↔ DTO
- [ ] Migrar gradualmente módulo por módulo

**Prioridade:** BAIXA (refatoração futura)  
**Estimativa:** 10 dias  
**Dependências:** Todas as US anteriores

---

## 🔍 REVISÕES DE CÓDIGO NECESSÁRIAS

### 🔒 REV-001: Segurança - Credenciais Expostas
**Problema:** Senha do PostgreSQL e chave JWT hardcoded em `application.properties`

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
**Arquivo:** `application.properties` (linhas 4-5, 19)

---

### 🔒 REV-002: Segurança - Rotas Desprotegidas
**Problema:** SecurityConfig com `.requestMatchers("/**").permitAll()` torna JWT ineficaz

**Ações Necessárias:**
- [ ] Remover permitAll() global da linha 40
- [ ] Definir rotas públicas explicitamente (/api/usuario/login)
- [ ] Proteger rotas de cliente com hasRole("CLIENTE")
- [ ] Proteger rotas de vendedor com hasRole("VENDEDOR")
- [ ] Proteger rotas admin com hasRole("ADMIN")
- [ ] Testar acesso não autorizado retorna 403

**Prioridade:** CRÍTICA  
**Risco:** Segurança  
**Estimativa:** 2 horas  
**Arquivo:** `security/SecurityConfig.java` (linha 40)

---

### 🔧 REV-003: Arquitetura - Dependência JWT Duplicada
**Problema:** Dois providers JWT no pom.xml (jjwt 0.11.5 e auth0 4.5.0), apenas jjwt é usado

**Ações Necessárias:**
- [ ] Remover dependência com.auth0:java-jwt do pom.xml (linhas 82-86)
- [ ] Verificar se nenhum import de auth0 existe no código
- [ ] Executar mvn clean install para validar
- [ ] Atualizar documentação de dependências

**Prioridade:** MÉDIA  
**Risco:** Manutenibilidade  
**Estimativa:** 15 minutos  
**Arquivo:** `pom.xml` (linhas 82-86)

---

### 🔧 REV-004: Arquitetura - JwtUtil com Estado Estático
**Problema:** Chave JWT e métodos em campos static quebra injeção de dependência do Spring

**Ações Necessárias:**
- [ ] Remover modificador static da chave JWT (linha 15)
- [ ] Remover static dos métodos generateToken, extractUsername, validateToken
- [ ] Tornar JwtUtil completamente gerenciado pelo Spring
- [ ] Atualizar CustomUserDetailsService para injetar JwtUtil
- [ ] Validar que Spring gerencia o ciclo de vida

**Prioridade:** ALTA  
**Risco:** Testabilidade  
**Estimativa:** 1 hora  
**Arquivo:** `security/JwtUtil.java` (linhas 15, 24, 33, 41)

---

### 🔧 REV-005: Arquitetura - Entidades Expostas na API
**Problema:** Controllers retornam @Entity diretamente, acoplando API ao modelo de dados

**Ações Necessárias:**
- [ ] Criar DTOs de response para todos os endpoints GET
- [ ] Nunca retornar entidades JPA diretamente
- [ ] Implementar mappers para conversão
- [ ] Validar que senhaHash nunca é exposto em Usuario
- [ ] Atualizar todos os 9 controllers

**Prioridade:** ALTA  
**Risco:** Segurança + Manutenibilidade  
**Estimativa:** 6 horas  
**Relacionado:** US-022  
**Arquivos:** Todos os controllers

---

### 🔧 REV-006: Arquitetura - Relacionamentos JPA Primitivos
**Problema:** Pedido usa int primitivo para idCliente/idVendedor, Veiculo usa int para idProduto

**Ações Necessárias:**
- [ ] Substituir int idCliente por @ManyToOne Cliente cliente em Pedido
- [ ] Substituir int idVendedor por @ManyToOne Usuario vendedor em Pedido
- [ ] Substituir int idProduto por @ManyToOne Produto produto em Veiculo
- [ ] Adicionar FK em Endereco → Usuario
- [ ] Adicionar FK em Telefone → Usuario
- [ ] Criar migrations para alterar schema

**Prioridade:** ALTA  
**Risco:** Integridade de Dados  
**Estimativa:** 4 horas  
**Relacionado:** US-014, US-015  
**Arquivos:** `entities/Pedido.java`, `entities/Veiculo.java`

---

### 🔧 REV-007: Qualidade - Services Retornam Null
**Problema:** Services retornam null quando recurso não encontrado, em vez de lançar exceção

**Ações Necessárias:**
- [ ] Criar ResourceNotFoundException
- [ ] Substituir return null por throw new ResourceNotFoundException()
- [ ] Atualizar todos os 9 services
- [ ] Adicionar @ControllerAdvice para capturar exceção
- [ ] Retornar 404 com mensagem clara

**Prioridade:** MÉDIA  
**Risco:** Experiência do Usuário  
**Estimativa:** 2 horas  
**Relacionado:** US-023  
**Arquivos:** Todos os services

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
**Arquivos:** `VeiculoController.java`, `ItemPedidoController.java`

---

### 🔧 REV-009: Funcional - Veiculo Sem Enum de Status
**Problema:** Campo StatusVeiculo é String, deveria ser enum para validação

**Ações Necessárias:**
- [ ] Criar enum StatusFabricacao
- [ ] Substituir String StatusVeiculo por enum
- [ ] Adicionar @Enumerated(EnumType.STRING) na entidade
- [ ] Atualizar VeiculoService para validar transições
- [ ] Criar migration para alterar tipo da coluna

**Prioridade:** CRÍTICA  
**Risco:** Funcionalidade Principal  
**Estimativa:** 2 horas  
**Relacionado:** US-012  
**Arquivo:** `entities/Veiculo.java` (linha 18)

---

### 🔧 REV-010: Funcional - Usuario Sem Role
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
**Relacionado:** US-024  
**Arquivo:** `entities/Usuario.java`

---

### 🔧 REV-011: Funcional - ItemPedido Sem Relacionamento com Pedido
**Problema:** Relacionamento @ManyToOne com Pedido está comentado

**Ações Necessárias:**
- [ ] Descomentar @ManyToOne e @JoinColumn em ItemPedido (linhas 13-15)
- [ ] Adicionar @OneToMany em Pedido → List<ItemPedido>
- [ ] Configurar cascade e fetch adequados
- [ ] Testar criação de pedido com itens
- [ ] Atualizar PedidoService

**Prioridade:** ALTA  
**Risco:** Funcionalidade Principal  
**Estimativa:** 1 hora  
**Relacionado:** US-013  
**Arquivo:** `entities/ItemPedido.java` (linhas 13-15)

---

### 📊 REV-012: Performance - Ausência de Paginação
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
**Estimativa:** 3 horas  
**Relacionado:** US-025  
**Arquivos:** Todos os repositories, services e controllers

---

### 📋 REV-013: Documentação - API Sem Especificação OpenAPI
**Problema:** Endpoints não possuem documentação formal (Swagger/OpenAPI)

**Ações Necessárias:**
- [ ] Adicionar dependência springdoc-openapi-starter-webmvc-ui no pom.xml
- [ ] Configurar Swagger UI em /swagger-ui.html
- [ ] Adicionar @Operation em todos os endpoints
- [ ] Adicionar @Schema nos DTOs
- [ ] Documentar códigos de resposta (200, 400, 401, 404)
- [ ] Adicionar exemplos de request/response

**Prioridade:** MÉDIA  
**Risco:** Documentação  
**Estimativa:** 4 horas  
**Arquivos:** `pom.xml`, todos os controllers

---

### 🔧 REV-014: Qualidade - Falta de Validação em Veiculo e Produto
**Problema:** Entidades Veiculo e Produto não possuem validações Jakarta

**Ações Necessárias:**
- [ ] Adicionar @NotNull, @NotBlank em campos obrigatórios
- [ ] Adicionar @Min, @Max em campos numéricos (ano, chassi)
- [ ] Adicionar @Size em campos de texto
- [ ] Validar que chassi é único
- [ ] Testar validações nos controllers

**Prioridade:** MÉDIA  
**Risco:** Qualidade de Dados  
**Estimativa:** 1 hora  
**Arquivos:** `entities/Veiculo.java`, `entities/Produto.java`

---

### 🔧 REV-015: Arquitetura - Configuração MQTT Sem Implementação
**Problema:** Configurações MQTT no application.properties mas sem código de integração

**Ações Necessárias:**
- [ ] Adicionar dependências spring-integration-mqtt no pom.xml
- [ ] Criar MqttConfig.java
- [ ] Implementar adapters MQTT
- [ ] Ou remover configurações se não for implementar agora

**Prioridade:** BAIXA (informativo)  
**Risco:** Configuração Órfã  
**Estimativa:** N/A  
**Relacionado:** US-016, US-017  
**Arquivo:** `application.properties` (linhas 20-24)

---

## 📊 MÉTRICAS DO PROJETO

### Entregas Concluídas
- **Total:** 11 histórias de usuário
- **Pontos de Função:** 
  - Backend base com Spring Boot 3.3.1 + PostgreSQL
  - Autenticação JWT completa
  - CRUD completo: Usuários (PF/PJ), Endereços, Telefones, Pedidos
  - Novas entidades: Veículo, Produto, ItemPedido
  - Configuração MQTT preparada
  - Documentação de arquitetura

### Entregas Pendentes (Abril 2026)
- **Total:** 15 histórias de usuário
- **Prioridade Crítica:** 3 (US-012, US-016, US-017, US-024)
- **Prioridade Alta:** 7 (US-013, US-14, US-15, US-18, US-21, US-22, US-23)
- **Prioridade Média:** 4 (US-19, US-20, US-25)
- **Prioridade Baixa:** 1 (US-26 - refatoração hexagonal)

### Revisões de Código
- **Total:** 15 revisões identificadas
- **Prioridade Crítica:** 4 (REV-001, REV-002, REV-009, REV-010)
- **Prioridade Alta:** 4 (REV-004, REV-005, REV-006, REV-011)
- **Prioridade Média:** 6 (REV-003, REV-007, REV-012, REV-013, REV-014)
- **Prioridade Baixa:** 1 (REV-008, REV-015)

### Estimativa Total (Abril 2026)
- **Desenvolvimento:** ~28 dias de trabalho
- **Revisões Críticas:** ~7 horas
- **Revisões Altas:** ~12 horas
- **Revisões Médias:** ~11 horas
- **Risco Principal:** Integração MQTT (US-16, US-17, US-18)

### Progresso Atual
- **Entidades Implementadas:** 9/12 (75%)
- **Controllers Implementados:** 9/12 (75%)
- **Integração MQTT:** 0% (apenas configuração)
- **DTOs Implementados:** 1/30+ (3%)
- **Segurança:** 40% (JWT ok, roles e proteção de rotas pendentes)
- **Arquitetura Hexagonal:** 0% (planejada para futuro)

---

## 🎯 ROADMAP SUGERIDO - ABRIL 2026

### Semana 1 (01-07 Abril) - SEGURANÇA E FUNDAÇÃO
**Foco:** Corrigir problemas críticos de segurança e preparar base

1. ✅ **REV-001:** Remover credenciais expostas (1h) - CRÍTICO
2. ✅ **REV-002:** Proteger rotas no SecurityConfig (2h) - CRÍTICO
3. ✅ **REV-010:** Adicionar roles em Usuario (2h) - CRÍTICO
4. ✅ **US-024:** Implementar sistema de roles completo (2 dias)
5. ✅ **REV-009:** Criar enum StatusFabricacao (2h) - CRÍTICO
6. ✅ **US-012:** Implementar máquina de estados (2 dias)

**Entregáveis:** Sistema com segurança básica, roles funcionando, status de fabricação modelado

---

### Semana 2 (08-14 Abril) - RELACIONAMENTOS E DTOs
**Foco:** Corrigir modelo de dados e criar camada de DTOs

1. ✅ **REV-011:** Descomentar relacionamento ItemPedido → Pedido (1h)
2. ✅ **US-013:** Implementar relacionamento bidirecional (1 dia)
3. ✅ **REV-006:** Corrigir relacionamentos primitivos (4h)
4. ✅ **US-014:** Implementar relacionamentos JPA completos (3 dias)
5. ✅ **US-015:** Relacionar Veiculo → Produto (1 dia)
6. ✅ **US-22:** Iniciar criação de DTOs (2 dias)

**Entregáveis:** Modelo de dados com integridade referencial, DTOs básicos criados

---

### Semana 3 (15-21 Abril) - INTEGRAÇÃO MQTT
**Foco:** Implementar pipeline IoT completo

1. ✅ **US-016:** Adicionar dependências e criar MqttConfig (2 dias)
2. ✅ **US-017:** Implementar MqttFabricacaoAdapter (3 dias)
3. ✅ **US-018:** Implementar MqttSensorAdapter (2 dias)
4. ✅ **REV-004:** Refatorar JwtUtil (1h)
5. ✅ **REV-003:** Remover dependência JWT duplicada (15min)

**Entregáveis:** Sistema recebendo eventos MQTT, atualizando status automaticamente

---

### Semana 4 (22-30 Abril) - FEATURES E QUALIDADE
**Foco:** Completar funcionalidades e melhorar qualidade

1. ✅ **US-019:** Sistema de alertas IoT (2 dias)
2. ✅ **US-020:** Módulo de notícias (2 dias)
3. ✅ **US-021:** Dashboard agregado (1 dia)
4. ✅ **US-023:** Tratamento global de erros (1 dia)
5. ✅ **US-025:** Paginação de listagens (2 dias)
6. ✅ **REV-005:** Finalizar DTOs em todos controllers (4h)
7. ✅ **REV-007:** Implementar exceções customizadas (2h)
8. ✅ **REV-013:** Adicionar Swagger/OpenAPI (4h)
9. ✅ **REV-014:** Adicionar validações em Veiculo/Produto (1h)

**Entregáveis:** Sistema completo com IoT, dashboard, alertas, documentação API

---

## 📝 NOTAS IMPORTANTES

### Conformidade com Requisitos da UC
- **Cobertura Atual:** 91% (39% conforme + 52% parcial)
- **Lacunas Principais:**
  - Camada Cloud (deploy e infraestrutura)
  - Interface Industrial (KPIs e alertas operacionais)
  - Diagramas formais (ER, camadas, OpenAPI)

### Novidades Implementadas (desde última análise)
✅ **Entidade Veiculo:** Criada com CRUD completo  
✅ **Entidade Produto:** Criada com CRUD completo  
✅ **Entidade ItemPedido:** Criada com relacionamento @ManyToOne Produto  
✅ **Configuração MQTT:** Preparada no application.properties  
✅ **9 Controllers:** Todos com endpoints REST funcionais  
✅ **9 Services:** Camada de negócio implementada  
✅ **9 Repositories:** Acesso a dados via Spring Data JPA  

### Pendências Críticas Identificadas
❌ **Enum StatusFabricacao:** Campo é String, deveria ser enum  
❌ **Roles em Usuario:** Não existe campo de perfil  
❌ **Relacionamentos JPA:** Uso de int primitivo em vez de @ManyToOne  
❌ **ItemPedido → Pedido:** Relacionamento comentado  
❌ **Segurança:** Todas as rotas estão abertas (permitAll)  
❌ **Credenciais:** Senha do PostgreSQL e JWT expostas  
❌ **DTOs:** Apenas LoginRequest existe, entidades expostas na API  
❌ **MQTT:** Configuração existe mas sem implementação  

### Dependências Externas
- **Mosquitto MQTT Broker:** Deve ser instalado e configurado localmente
- **ESP32:** Simulador de linha de produção (hardware externo)
- **PostgreSQL:** Configurado em localhost:5432/db_experience

### Riscos Identificados
1. **Integração MQTT:** Primeira vez que a equipe trabalha com MQTT no Spring
2. **Relacionamentos JPA:** Mudanças no schema podem quebrar dados existentes
3. **Credenciais Expostas:** Risco de segurança ativo no repositório
4. **Falta de DTOs:** SenhaHash pode estar sendo exposto nas APIs

### Recomendações Imediatas
1. **URGENTE:** Executar REV-001 e REV-002 (segurança)
2. **URGENTE:** Executar REV-010 (adicionar roles)
3. **IMPORTANTE:** Executar REV-009 (enum de status)
4. **IMPORTANTE:** Executar REV-011 (descomentar relacionamento ItemPedido)
5. **PLANEJAMENTO:** Definir estratégia de migration para mudanças no schema

---

## 🔄 COMPARAÇÃO COM ANÁLISE ANTERIOR

### O que foi implementado desde a última análise:
- ✅ Entidade Veiculo (era US-009, agora concluída como US-008)
- ✅ Entidade Produto (nova, não estava prevista)
- ✅ Entidade ItemPedido (nova, não estava prevista)
- ✅ Configuração MQTT no properties (era US-010, parcialmente concluída como US-011)
- ✅ 3 novos controllers completos
- ✅ 3 novos services completos
- ✅ 3 novos repositories completos

### O que ainda não foi implementado:
- ❌ Enum StatusFabricacao (previsto, não implementado)
- ❌ Entidade StatusHistorico (previsto, não implementado)
- ❌ Dependências MQTT no pom.xml (previsto, não implementado)
- ❌ MqttConfig.java (previsto, não implementado)
- ❌ Adapters MQTT (previsto, não implementado)
- ❌ Entidade LeituraSensor (previsto, não implementado)
- ❌ Entidade Noticia (previsto, não implementado)
- ❌ Sistema de roles (previsto, não implementado)
- ❌ DTOs de response (previsto, não implementado)
- ❌ Tratamento global de erros (previsto, não implementado)

### Progresso Geral:
- **Antes:** 7 US concluídas
- **Agora:** 11 US concluídas (+4)
- **Taxa de Conclusão:** ~27% das US pendentes foram implementadas
- **Foco da Equipe:** Modelagem de dados (entidades) e CRUD básico
- **Próximo Foco Sugerido:** Segurança, relacionamentos JPA e integração MQTT

---

**Última Atualização:** 10 de Abril de 2026  
**Responsável:** Equipe Toyota Experience  
**Próxima Revisão:** 17 de Abril de 2026  
**Status Geral:** 🟡 Em Progresso - Fundação sólida, pendências críticas de segurança e integração
