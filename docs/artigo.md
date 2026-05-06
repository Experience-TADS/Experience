# CENTRO UNIVERSITÁRIO SENAI SÃO PAULO - UNISENAI-SP
## CAMPUS SOROCABA - SANTA ROSÁLIA

**GRADUAÇÃO EM ANÁLISE E DESENVOLVIMENTO DE SISTEMAS**

---

# Toyota Experience: Desenvolvimento de uma Plataforma Full-Stack para Gestão de Concessionária com Arquitetura de Microsserviços

**Toyota Experience: Full-Stack Platform Development for Dealership Management with Microservices Architecture**

**[Nome do Aluno]** ¹
**[Nome do Orientador]** ²

---

## RESUMO

Este artigo apresenta o desenvolvimento do sistema Toyota Experience, um Projeto Integrador realizado no Centro Universitário SENAI São Paulo. O sistema consiste em uma plataforma digital completa para gestão de concessionária, composta por um backend em Java com Spring Boot 3.3.1 e um frontend web desenvolvido em Next.js com TypeScript. O backend implementa uma API REST com autenticação JWT via Spring Security, gerenciamento de clientes Pessoa Física e Pessoa Jurídica com herança JPA estratégia JOINED, e módulos de Pedido, Produto, Veículo e ItemPedido. O frontend oferece interfaces distintas para clientes e vendedores, incluindo acompanhamento em tempo real do status de produção do veículo, dashboard de vendas e gestão de pedidos. A infraestrutura utiliza Docker Compose com PostgreSQL para produção e banco H2 em memória para desenvolvimento, com deploy realizado na AWS (EC2 + RDS) e Vercel. Os resultados demonstram uma plataforma funcional, segura e alinhada aos princípios da Indústria 4.0.

**Palavras-chave:** Spring Boot. Next.js. Microsserviços. API REST. JWT. JPA. Docker. AWS. TypeScript.

---

## ABSTRACT

This article presents the development of the Toyota Experience system, an Integrative Project carried out at Centro Universitário SENAI São Paulo. The system consists of a complete digital platform for dealership management, composed of a Java backend with Spring Boot 3.3.1 and a web frontend developed in Next.js with TypeScript. The backend implements a REST API with JWT authentication via Spring Security, management of Individual and Corporate clients using JPA JOINED inheritance strategy, and modules for Orders, Products, Vehicles, and Order Items. The frontend provides distinct interfaces for customers and salespeople, including real-time vehicle production status tracking, sales dashboard, and order management. The infrastructure uses Docker Compose with PostgreSQL for production and an in-memory H2 database for development, deployed on AWS (EC2 + RDS) and Vercel. Results demonstrate a functional, secure platform aligned with Industry 4.0 principles.

**Keywords:** Spring Boot. Next.js. Microservices. REST API. JWT. JPA. Docker. AWS. TypeScript.

---

## 1 INTRODUÇÃO

A transformação digital no setor automotivo tem impulsionado a criação de plataformas que conectam fabricantes, concessionárias e consumidores em um ecossistema digital integrado. A Toyota, referência mundial em manufatura enxuta e inovação tecnológica, representa o contexto ideal para o desenvolvimento de um sistema que aplique os conceitos da Indústria 4.0 em um ambiente acadêmico prático.

O Projeto Integrador Toyota Experience foi desenvolvido no curso de Análise e Desenvolvimento de Sistemas do SENAI Sorocaba, com o objetivo de simular uma plataforma real de concessionária digital. O sistema abrange desde o cadastro e autenticação de clientes até o acompanhamento do ciclo completo de produção e entrega de veículos.

### 1.1 Problema de pesquisa

Como desenvolver uma plataforma digital full-stack para gestão de concessionária que integre backend com microsserviços, autenticação segura, gestão de clientes PF e PJ, controle de pedidos e veículos, e um frontend responsivo com interfaces distintas para clientes e vendedores?

### 1.2 Objetivos

**Objetivo Geral:**
Desenvolver uma plataforma digital completa para gestão de concessionária Toyota, com backend em Spring Boot, frontend em Next.js e infraestrutura na nuvem AWS.

**Objetivos Específicos:**
- Implementar microsserviço de identidade com herança JPA para Pessoa Física e Pessoa Jurídica;
- Desenvolver módulos de Pedido, Produto, Veículo e ItemPedido com CRUD completo via API REST;
- Implementar autenticação e autorização stateless com JWT e Spring Security com controle de roles;
- Criar interface web responsiva com Next.js para clientes e vendedores;
- Implementar rastreamento do ciclo de fabricação do veículo com enum StatusFabricacao;
- Realizar deploy da aplicação na AWS (EC2 + RDS PostgreSQL) e Vercel.

### 1.3 Justificativa

O projeto aplica na prática os principais conceitos estudados ao longo do curso, integrando desenvolvimento backend, frontend, banco de dados, segurança e infraestrutura em nuvem em um único sistema coeso. A escolha da Toyota como contexto representa um caso real de empresa que investe em digitalização de processos, rastreabilidade de produção e experiência do cliente, tornando o projeto academicamente relevante e tecnicamente desafiador.

---

## 2 FUNDAMENTAÇÃO TEÓRICA

### 2.1 Indústria 4.0

A Indústria 4.0 representa a quarta revolução industrial, caracterizada pela integração de sistemas ciberfísicos, Internet das Coisas, computação em nuvem e inteligência artificial nos processos produtivos (SCHWAB, 2016). No contexto automotivo, essa transformação se manifesta na digitalização da cadeia de produção, rastreabilidade de veículos e personalização da experiência do cliente. O Toyota Experience simula exatamente esse cenário: um sistema que conecta o cliente ao status de fabricação do seu veículo, passando por etapas modeladas no enum `StatusFabricacao`.

### 2.2 IoT

A Internet das Coisas permite que objetos físicos sejam conectados à internet, coletando e transmitindo dados em tempo real (ATZORI et al., 2010). O sistema modela o ciclo de vida do veículo por meio da entidade `StatusHistorico`, que registra cada mudança de status ao longo da linha de produção. Essa modelagem cria a base para uma futura integração com sensores IoT que atualizem automaticamente o status do veículo conforme ele avança na linha de montagem.

### 2.3 Cloud Computing

A computação em nuvem oferece recursos de infraestrutura sob demanda, com escalabilidade e alta disponibilidade (MELL; GRANCE, 2011). O projeto realizou deploy completo na AWS: banco de dados gerenciado no **RDS PostgreSQL** (instância db.t4g.micro) e backend no **EC2** (instância t3.micro com Amazon Linux 2023). O frontend foi publicado na **Vercel**, plataforma especializada em Next.js com distribuição global via CDN.

### 2.4 UX Industrial

A experiência do usuário em contextos industriais envolve tanto as interfaces visuais quanto a qualidade das APIs que as alimentam (NORMAN, 2013). O projeto implementa interfaces distintas para dois perfis: o cliente, que acompanha o status do seu veículo e agenda a retirada, e o vendedor, que acessa dashboard com métricas de vendas, lista de pedidos e gestão de clientes. A API adota padrão Request/Response com DTOs dedicados para cada operação.

### 2.5 Integração Vertical e Horizontal

A integração horizontal conecta sistemas do mesmo nível hierárquico, enquanto a integração vertical conecta camadas distintas da produção ao ERP (KAGERMANN et al., 2013). O diagrama UML do projeto ilustra essa arquitetura: o módulo de Identidade se comunica com os módulos de Pedidos e Veículos apenas por meio de IDs de referência, garantindo desacoplamento. O frontend Next.js consome a API REST do backend, representando a integração vertical entre a camada de apresentação e a camada de negócio.

### 2.6 Scrum

O Scrum é um framework ágil para gestão de projetos complexos, baseado em sprints, revisões incrementais e colaboração contínua (SCHWABER; SUTHERLAND, 2020). O desenvolvimento foi organizado em branches por issue no Git — com mais de 20 branches criadas para funcionalidades específicas como `issue151/relacionamento-pedido`, `issue152/enum-fabricacao`, `issue165/roles-and-autorization`, `issue212/cors-backend` — evidenciando uma abordagem ágil com entregas incrementais e rastreabilidade completa.

---

## 3 METODOLOGIA

O desenvolvimento seguiu uma abordagem ágil com controle de versão por branches no Git, onde cada issue/funcionalidade foi desenvolvida em uma branch separada e integrada ao branch principal após revisão.

**Stack Backend:**

| Componente | Tecnologia |
|------------|------------|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.3.1 |
| Gerenciador | Apache Maven |
| Persistência | Spring Data JPA / Hibernate |
| Banco (dev) | H2 Database (em memória) |
| Banco (produção) | PostgreSQL via AWS RDS |
| Segurança | Spring Security + JWT (jjwt 0.11.5) |
| Validação | Bean Validation (Jakarta) |
| Boilerplate | Lombok 1.18.36 |

**Stack Frontend:**

| Componente | Tecnologia |
|------------|------------|
| Framework | Next.js 16 (App Router) |
| Linguagem | TypeScript |
| Estilização | Tailwind CSS v4 |
| Ícones | Lucide React |
| Gráficos | Recharts |

**Infraestrutura:**

| Componente | Tecnologia |
|------------|------------|
| Backend | AWS EC2 t3.micro (Amazon Linux 2023) |
| Banco | AWS RDS PostgreSQL db.t4g.micro |
| Frontend | Vercel (CDN global) |
| Containerização local | Docker / Docker Compose |
| Controle de versão | Git / GitHub |

O padrão arquitetural do backend segue três camadas: **Controller** → **Service** → **Repository**, com camada adicional de **DTOs** (Request/Response) e **Mappers** para separar a representação da API das entidades do banco.

---

## 4 DESENVOLVIMENTO

### 4.1 Arquitetura Geral do Sistema

```
[Frontend - Next.js / Vercel]
          │
          ▼ HTTP/REST + JWT
[Backend - Spring Boot / AWS EC2]
          │
          ▼ JPA/Hibernate
[Banco - PostgreSQL / AWS RDS]
```

### 4.2 Modelagem de Dados

O sistema contempla os seguintes módulos com suas entidades:

**Módulo de Identidade:** `Usuario`, `PessoaFisica`, `PessoaJuridica`, `Endereco`, `Telefone`

**Módulo de Vendas:** `Pedido`, `ItemPedido`, `Produto`, `Veiculo`

**Módulo de Fabricação:** `StatusHistorico`, `StatusFabricacao` (enum)

**Segurança:** `UserRole` (enum com roles de autorização)

A herança entre `Usuario` e suas subclasses utiliza estratégia `JOINED`:

```
usuario (tabela base)
├── pessoa_fisica  → cpf (único, validado com @CPF)
└── pessoa_juridica → cnpj (único, validado com @CNPJ), razao_social
```

### 4.3 Padrão Request/Response com DTOs e Mappers

O backend adota DTOs dedicados para entrada e saída de dados, separando a API das entidades JPA:

- **Request DTOs:** `UsuarioRequest`, `PedidoRequest`, `ProdutoRequest`, `VeiculoRequest`, `ItemPedidoRequest`, `EnderecoRequest`, `TelefoneRequest`, `LoginRequest`
- **Response DTOs:** `UsuarioResponse`, `PedidoResponse`, `ProdutoResponse`, `VeiculoResponse`, `ItemPedidoResponse`, `EnderecoResponse`, `TelefoneResponse`, `LoginResponse`
- **Mappers:** classes dedicadas para conversão entre entidade e DTO em cada módulo

### 4.4 API REST

| Endpoint | Operações |
|----------|-----------|
| `/api/usuario` | GET, POST, PUT, DELETE |
| `/api/pessoaFisica` | GET, POST, PUT, DELETE |
| `/api/pessoaJuridica` | GET, POST, PUT, DELETE |
| `/api/endereco` | GET, POST, PUT, DELETE |
| `/api/telefone` | GET, POST, PUT, DELETE |
| `/api/pedido` | GET, POST, PUT, DELETE |
| `/api/produto` | GET, POST, PUT, DELETE |
| `/api/veiculo` | GET, POST, PUT, DELETE |
| `/api/itemPedido` | GET, POST, PUT, DELETE |
| `/api/statusHistorico` | GET, POST |
| `/api/usuario/login` | POST (autenticação) |

### 4.5 Segurança — JWT e Roles

A autenticação utiliza Spring Security stateless com JWT (jjwt 0.11.5):

1. Cliente envia credenciais para `/api/usuario/login`
2. `CustomUserDetailsService` valida com `BCryptPasswordEncoder`
3. `JwtUtil` gera token HMAC-SHA256 válido por 24h
4. `JwtAuthFilter` intercepta e valida o token em cada requisição
5. `UserRole` controla autorização por perfil (cliente/vendedor/admin)

### 4.6 Rastreamento de Fabricação

A entidade `StatusHistorico` registra cada mudança de status do veículo ao longo da produção. O enum `StatusFabricacao` define as etapas: Compra realizada → Pedido encaminhado → Produção → Qualidade → Transporte → Concessionária → Retirada.

### 4.7 Frontend

**Área do Cliente:**
- `/Login` — autenticação com redirecionamento por perfil
- `/Cliente/perfil` — dados pessoais e preferências
- `/Cliente/Acompanhamento` — timeline de status do veículo com 7 etapas
- `/Cliente/Loja` — catálogo de veículos

**Área do Vendedor:**
- `/Vendedor/Dashbord` — métricas e gráfico de vendas (Recharts)
- `/Vendedor/Pedidos` — tabela com busca e modal de detalhes
- `/Vendedor/Clientes` — gestão de clientes
- `/Vendedor/Perfil` — dados do vendedor

### 4.8 Deploy na AWS

- **RDS:** banco PostgreSQL gerenciado, instância db.t4g.micro, região us-east-1
- **EC2:** servidor t3.micro com Amazon Linux 2023, backend configurado como serviço systemd com auto-start
- **Security Groups:** regras de firewall configuradas para portas 22 (SSH), 80 (HTTP) e 5432 (PostgreSQL)
- **Vercel:** frontend publicado via Deploy Hook da branch `feature/FrontEndWeb`

---

## 5 RESULTADOS E DISCUSSÕES

**Backend:**
- API REST com mais de 40 endpoints em 10 controllers
- Padrão Request/Response com DTOs e Mappers em todos os módulos
- Autenticação JWT stateless com controle de roles
- Rastreamento de fabricação com histórico de status
- Deploy funcionando na AWS EC2 com conexão ao RDS PostgreSQL

**Frontend:**
- Interface responsiva com Tailwind CSS
- Dois perfis de usuário com rotas distintas
- Timeline visual do ciclo de fabricação em 7 etapas
- Dashboard com gráfico de vendas mensais
- Deploy na Vercel com URL pública

**Desafios superados:**
- Configuração da herança JPA com Spring Security
- Implementação de roles e autorização por perfil
- Configuração do CORS para comunicação frontend-backend
- Deploy na AWS com configuração de Security Groups e serviço systemd

---

## 6 DISCUSSÃO INTERDISCIPLINAR

**Banco de Dados:** Modelagem relacional com JPA/Hibernate, herança JOINED, relacionamentos ManyToOne, normalização e integridade referencial.

**Engenharia de Software:** Padrão em três camadas + DTOs/Mappers, princípios SOLID, separação de responsabilidades, Git Flow por issue.

**Desenvolvimento Web:** Next.js App Router, TypeScript, Tailwind CSS, componentes reutilizáveis, gerenciamento de estado com React hooks.

**Redes e Infraestrutura:** AWS EC2 e RDS, Security Groups, Docker, comunicação HTTP/REST, deploy em nuvem.

**Segurança:** JWT stateless, BCrypt, roles de autorização, validação de CPF/CNPJ, CORS configurado.

**Gestão de Projetos:** Scrum com branches por issue, mais de 20 branches de funcionalidades, rastreabilidade completa no GitHub.

---

## 7 CONCLUSÃO

O Projeto Integrador Toyota Experience demonstrou na prática a viabilidade de desenvolver uma plataforma digital completa para gestão de concessionária, integrando tecnologias modernas de backend, frontend e infraestrutura em nuvem em um sistema coeso e funcional.

A arquitetura adotada — Spring Boot no backend, Next.js no frontend, PostgreSQL no banco e AWS na infraestrutura — reflete as escolhas tecnológicas do mercado atual. O padrão Request/Response com DTOs e Mappers, a autenticação JWT com roles, o rastreamento de fabricação com histórico de status e o deploy completo na nuvem são os diferenciais técnicos do projeto.

Como trabalho futuro, a integração real entre o frontend e o backend (substituição dos dados mockados), a implementação de testes automatizados, a adoção de CI/CD e a integração com sensores IoT para atualização automática do status de fabricação completariam o ecossistema Toyota Experience.

---

## REFERÊNCIAS

ATZORI, L.; IERA, A.; MORABITO, G. The Internet of Things: A survey. **Computer Networks**, v. 54, n. 15, p. 2787–2805, 2010.

KAGERMANN, H.; WAHLSTER, W.; HELBIG, J. **Recommendations for implementing the strategic initiative Industrie 4.0**. Frankfurt: Acatech, 2013.

MELL, P.; GRANCE, T. **The NIST Definition of Cloud Computing**. Gaithersburg: NIST, 2011. (Special Publication 800-145).

NORMAN, D. **The Design of Everyday Things**. Revised ed. New York: Basic Books, 2013.

SCHWAB, K. **The Fourth Industrial Revolution**. Geneva: World Economic Forum, 2016.

SCHWABER, K.; SUTHERLAND, J. **The Scrum Guide**. Scrum.org, 2020. Disponível em: https://scrumguides.org. Acesso em: abr. 2026.

WALLS, C. **Spring Boot in Action**. Shelter Island: Manning Publications, 2016.

WIERUCH, R. **The Road to Next.js**. Leanpub, 2023.

---

## AGRADECIMENTOS

[Espaço reservado para agradecimentos ao orientador, colegas de equipe, instituição SENAI e demais colaboradores do projeto.]

---

## SOBRE O(S) AUTOR(ES)

**[Nome do Aluno]** — Graduando em Análise e Desenvolvimento de Sistemas no Centro Universitário SENAI São Paulo, Campus Sorocaba. Desenvolvedor do Projeto Integrador Toyota Experience. E-mail: [email do aluno]

**[Nome do Orientador]** — Docente do Centro Universitário SENAI São Paulo. [Titulação e área de atuação]. E-mail: [email institucional]

---

*¹ Graduando em Análise e Desenvolvimento de Sistemas — UNISENAI-SP. E-mail: [email do aluno]*

*² Docente — UNISENAI-SP. E-mail: [email do orientador]*
