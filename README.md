# Experience

Projeto Integrador Toyota — SENAI. API REST para gestão do ciclo de vida de veículos, desde o cadastro de clientes e produtos até o acompanhamento em tempo real das etapas de fabricação, integrando dispositivos IoT (ESP32 via Node-RED/MQTT) à linha de produção.

## Visão geral

A aplicação centraliza quatro domínios principais:

- **Cadastro de usuários** (pessoa física e jurídica) com autenticação JWT e controle de acesso por papéis.
- **Catálogo de produtos e veículos**, cada veículo vinculado a um produto e identificado por um chassi.
- **Pedidos de venda**, relacionando cliente, vendedor e itens.
- **Rastreamento de fabricação**, com histórico de status atualizado manualmente por ADMIN/VENDEDOR ou automaticamente por eventos IoT recebidos do chão de fábrica.

## Tecnologias

- **Java 17** e **Spring Boot 3.3.1**
- **Spring Web** (REST), **Spring Data JPA**, **Spring Security**, **Spring Validation**
- **JWT** (biblioteca `jjwt` 0.11.5) para autenticação stateless
- **PostgreSQL** em produção, **H2** em memória para testes
- **springdoc-openapi** (Swagger UI) para documentação da API
- **Lombok** para redução de boilerplate
- **Spring Mail** e **OAuth2 Client** (dependências disponíveis para extensões)
- **Maven** para build e **Docker** (multi-stage) para empacotamento

## Arquitetura

O código segue uma organização em camadas dentro do pacote `com.senai.experience`:

```
config/        Seeders de dados e tratamento global de exceções
controllers/   Endpoints REST
DTO/
  request/     Objetos de entrada (validados com Bean Validation)
  response/    Objetos de saída (não expõem dados sensíveis como senha)
entities/      Entidades JPA (modelo de domínio)
  role/        Enum de papéis de usuário
mappers/       Conversão entre entidades e DTOs
repositories/  Interfaces Spring Data JPA
security/      Configuração de segurança, filtro e utilitário JWT
services/      Regras de negócio
```

### Modelo de domínio

- **Usuario** — entidade base com herança `JOINED`. Campos: nome, email (único), senhaHash, dataNascimento, ativo e papel (`role`).
  - **PessoaFisica** — adiciona `cpf` (validado com `@CPF`).
  - **PessoaJuridica** — adiciona `cnpj` (validado com `@CNPJ`) e `razaoSocial`.
- **Endereco** e **Telefone** — dados de contato vinculados a um usuário.
- **Produto** — modelo, cor, versão e ano.
- **Veiculo** — vincula um produto, possui `chassi`, `statusVeiculo` e histórico de fabricação; pode ser associado a um pedido.
- **Pedido** — relaciona cliente e vendedor, com data, valor total e itens.
- **ItemPedido** — produto e quantidade dentro de um pedido.
- **StatusHistorico** — registra cada mudança de status de um veículo com data/hora.
- **EtapaTemplate** — modelo de etapas de fabricação (usado no seeding).

### Papéis de usuário (`UserRole`)

- `CLIENTE` — acessa apenas os próprios pedidos.
- `VENDEDOR` — gerencia produtos, pedidos e avança status de fabricação.
- `ADMIN` — acesso total, incluindo ativação/desativação de usuários.
- `IOT` — reservado para dispositivos que reportam eventos de fabricação.

### Fluxo de status de fabricação

O status de um veículo segue uma sequência controlada por validação de transição:

```
AGUARDANDO → MONTAGEM_ESTRUTURAL → PINTURA → INSTALACAO_MOTOR →
ACABAMENTO_INTERNO → INSPECAO_FINAL → LIBERACAO_TRANSPORTE → ENTREGUE
```

Qualquer etapa pode transitar para `CANCELADO`. Transições fora dessa ordem são rejeitadas. Eventos vindos do ESP32 (via Node-RED/MQTT) chegam ao endpoint `POST /api/veiculo/nodered/evento`, que mapeia a etapa/status do dispositivo para o `StatusFabricacao` correspondente.

## Segurança

- Autenticação **stateless** via token JWT enviado no cabeçalho `Authorization: Bearer <token>`.
- O filtro `JwtAuthFilter` valida o token antes de extrair as claims, ignorando tokens malformados sem interromper a requisição.
- Autorização baseada em papéis, configurada em `SecurityConfig` e reforçada com `@PreAuthorize` em endpoints sensíveis.
- Senhas armazenadas com **BCrypt**; DTOs de resposta nunca expõem o hash.

### Rotas públicas

- `POST /api/usuario` — cadastro de usuário
- `POST /api/usuario/login` — autenticação
- `GET /api/usuario/me` — dados do usuário autenticado (trata token ausente/ inválido)
- `POST /api/pessoaFisica` e `POST /api/pessoaJuridica` — auto-cadastro
- `POST /api/veiculo/nodered/evento` — recepção de eventos IoT
- Swagger UI e `v3/api-docs`

Demais rotas exigem autenticação e, em muitos casos, papel específico.

## Principais endpoints

| Recurso | Rota base | Observações |
|---|---|---|
| Usuários | `/api/usuario` | login, `/me`, ativar/desativar (ADMIN) |
| Pessoa Física | `/api/pessoaFisica` | POST público |
| Pessoa Jurídica | `/api/pessoaJuridica` | POST público |
| Endereços | `/api/endereco` | autenticado |
| Telefones | `/api/telefones` | autenticado |
| Produtos | `/api/produto` | leitura autenticada; escrita ADMIN/VENDEDOR |
| Veículos | `/api/veiculo` | inclui busca por chassi |
| Status de fabricação | `/api/veiculo/{id}/status` | leitura autenticada; POST ADMIN/VENDEDOR |
| Itens de pedido | `/api/itens-pedido` | autenticado |
| Pedidos | `/api/pedido` | `/meus-pedidos` para cliente; demais VENDEDOR/ADMIN |

> As listagens (`GET` de coleções) retornam objetos paginados do Spring Data. O array de resultados fica no campo `content`, junto de metadados como `totalElements`, `totalPages`, `size` e `number`.

## Configuração

As principais configurações ficam em `api/experience/src/main/resources/application.properties`:

- **Banco de dados** — conexão PostgreSQL (`spring.datasource.*`).
- **JPA** — `ddl-auto=update` e dialeto PostgreSQL.
- **JWT** — `jwt.secret` (chave Base64 de no mínimo 256 bits).
- **E-mail** — SMTP (Gmail por padrão; credenciais em branco).
- **MQTT** — broker e tópicos de fabricação para integração IoT.

> Recomendação: em produção, mova segredos (senha do banco, `jwt.secret`, credenciais de e-mail) para variáveis de ambiente em vez de deixá-los no arquivo de propriedades.

O perfil de teste (`application-test.properties`) usa H2 em memória, dispensando um banco externo para rodar a suíte.

## Como executar

### Pré-requisitos

- JDK 17
- Maven 3.9+
- PostgreSQL em execução com um banco `db_experience` (ou ajuste a URL/credenciais)

### Rodando localmente

A partir de `api/experience`:

```bash
# Compilar e empacotar
mvn clean package

# Executar
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. A documentação interativa fica em `http://localhost:8080/swagger-ui.html`.

### Rodando os testes

```bash
mvn test
```

A suíte de integração usa H2 em memória e o perfil `test`, sem necessidade de banco externo.

### Docker

O `Dockerfile` (em `api/`) usa build multi-stage: compila com Maven/Temurin 17 e roda em uma imagem JRE mínima com usuário não-root e health check. Para construir e executar:

```bash
docker build -t experience -f api/Dockerfile api
docker run -p 8080:8080 experience
```

## Testes automatizados

O projeto conta com uma suíte de testes de integração (`src/test/.../Test`) cobrindo os controllers de usuários, pessoas física e jurídica, endereços, telefones, produtos, veículos, itens de pedido e pedidos. Os testes exercitam autenticação JWT, autorização por papel, validações de entrada e regras de negócio (por exemplo, transições de status e duplicidade de documentos). O utilitário `AuthHelper` cria usuários e obtém tokens para os cenários autenticados.

## Estrutura do repositório

```
Experience/
├── README.md
└── api/
    ├── Dockerfile
    └── experience/
        ├── pom.xml
        ├── src/main/java/com/senai/experience/   # código da aplicação
        ├── src/main/resources/                    # configuração
        └── src/test/java/com/senai/experience/    # testes de integração
```
