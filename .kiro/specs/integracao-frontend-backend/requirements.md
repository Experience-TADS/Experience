# Requirements Document — Integração Frontend-Backend Toyota Experience

## Introdução

Este documento mapeia o que já pode ser integrado entre o frontend Next.js e o backend Spring Boot, e o que ainda precisa ser implementado no backend para que o frontend funcione corretamente.

---

## Mapeamento de Endpoints Existentes no Backend

| Endpoint | Método | Descrição |
|---|---|---|
| `/api/usuario/login` | POST | Autenticação — retorna JWT + role |
| `/api/usuario/me` | GET | Dados do usuário autenticado via token |
| `/api/usuario` | GET | Lista todos os usuários |
| `/api/usuario/{id}` | GET / PUT / DELETE | CRUD de usuário |
| `/api/usuario` | POST | Cria novo usuário |
| `/api/pedido` | GET | Lista todos os pedidos |
| `/api/pedido/{id}` | GET / PUT / DELETE | CRUD de pedido |
| `/api/pedido` | POST | Cria pedido |
| `/api/pedido/meus-pedidos` | GET | Pedidos do usuário autenticado |
| `/api/veiculo` | GET | Lista todos os veículos |
| `/api/veiculo/{id}` | GET / PUT / DELETE | CRUD de veículo |
| `/api/veiculo/{veiculoId}/status` | GET | Histórico de status do veículo |
| `/api/veiculo/{veiculoId}/status` | POST | Atualiza status do veículo |
| `/api/produto` | GET / POST / PUT / DELETE | CRUD de produto |
| `/api/itens-pedido` | GET / POST / PUT / DELETE | CRUD de itens de pedido |

---

## Glossário

- **Frontend_App**: Aplicação Next.js com páginas para Cliente e Vendedor
- **Backend_API**: API REST Spring Boot com autenticação JWT
- **API_Client**: Serviço TypeScript centralizado para chamadas HTTP
- **Auth_Service**: Serviço de autenticação JWT no frontend
- **JWT_Token**: Token retornado pelo backend após login bem-sucedido
- **UserRole**: Enum do backend — `CLIENTE`, `VENDEDOR`, `ADMIN`, `IOT`
- **StatusFabricacao**: Enum do backend — `AGUARDANDO`, `EM_FABRICACAO`, `PINTURA`, `CONTROLE_QUALIDADE`, `CONCLUIDO`, `ENTREGUE`, `CANCELADO`

---

## Requisitos

### Requisito 1: Configuração CORS no Backend

**User Story:** Como desenvolvedor, quero configurar CORS no backend para que o frontend possa fazer requisições sem bloqueios.

#### Critérios de Aceitação

1. THE Backend_API SHALL accept requests from `http://localhost:3000`
2. THE Backend_API SHALL allow HTTP methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
3. THE Backend_API SHALL allow headers: Authorization, Content-Type, Accept
4. WHEN a preflight OPTIONS request is received, THE Backend_API SHALL respond with status 200

> **Status:** ❌ Não implementado — necessário para qualquer integração funcionar

---

### Requisito 2: API Client Service no Frontend

**User Story:** Como desenvolvedor, quero um serviço centralizado de comunicação com a API para reutilizar lógica de requisições HTTP em todas as páginas.

#### Critérios de Aceitação

1. THE Frontend_App SHALL provide an API_Client at `lib/api.ts` with typed methods for all backend endpoints
2. THE API_Client SHALL read base URL from `NEXT_PUBLIC_API_URL` environment variable, defaulting to `http://localhost:8080`
3. THE API_Client SHALL automatically include `Authorization: Bearer {token}` header when token exists in localStorage
4. THE API_Client SHALL export TypeScript interfaces matching backend DTOs: `LoginResponse`, `UsuarioResponse`, `PedidoResponse`, `VeiculoResponse`
5. WHEN response status is 401, THE API_Client SHALL clear localStorage and redirect to `/Login`

> **Status:** ❌ Não implementado — todas as páginas usam dados mockados

---

### Requisito 3: Integração da Página de Login

**User Story:** Como usuário, quero fazer login com email e senha para acessar o sistema com meu perfil correto.

#### Critérios de Aceitação

1. WHEN user submits the login form, THE Frontend_App SHALL call `POST /api/usuario/login` with `{ email, senha }`
2. WHEN login succeeds, THE Frontend_App SHALL store the JWT token in `localStorage` under key `token`
3. WHEN login succeeds, THE Frontend_App SHALL store the user role in `localStorage` under key `userRole`
4. WHEN role is `CLIENTE`, THE Frontend_App SHALL redirect to `/Cliente/Acompanhamento`
5. WHEN role is `VENDEDOR`, THE Frontend_App SHALL redirect to `/Vendedor/Dashbord`
6. WHEN role is `ADMIN`, THE Frontend_App SHALL redirect to `/Vendedor/Administracao`
7. WHEN login fails with status 401, THE Frontend_App SHALL display "Email ou senha inválidos"
8. THE Frontend_App SHALL show a loading state while the request is in progress

> **Status:** ❌ Não implementado — página de login não existe ainda no frontend (arquivo `app/Login/page.tsx` contém a tela de Administração por engano)

---

### Requisito 4: Integração da Página de Acompanhamento do Cliente

**User Story:** Como cliente, quero ver meus pedidos e acompanhar o status de fabricação do meu veículo.

#### Critérios de Aceitação

1. WHEN the page loads, THE Frontend_App SHALL call `GET /api/pedido/meus-pedidos` with the JWT token
2. THE Frontend_App SHALL display each pedido with its `id`, and the associated vehicle's `statusVeiculo`
3. WHEN user expands a pedido, THE Frontend_App SHALL call `GET /api/veiculo/{veiculoId}/status` to fetch the status history
4. THE Frontend_App SHALL map `StatusFabricacao` enum values to Portuguese labels:
   - `AGUARDANDO` → "Compra realizada"
   - `EM_FABRICACAO` → "Início de produção"
   - `PINTURA` → "Qualidade"
   - `CONTROLE_QUALIDADE` → "Vistoria"
   - `CONCLUIDO` → "Pedido pronto"
   - `ENTREGUE` → "Entregue"
5. WHEN the API call fails, THE Frontend_App SHALL display "Erro ao carregar pedidos"

> **Status:** ⚠️ Parcialmente possível — `GET /api/pedido/meus-pedidos` e `GET /api/veiculo/{id}/status` existem. Falta o frontend consumir esses endpoints.

---

### Requisito 5: Integração da Página de Pedidos do Vendedor

**User Story:** Como vendedor, quero visualizar todos os pedidos para gerenciar o fluxo de trabalho.

#### Critérios de Aceitação

1. WHEN the page loads, THE Frontend_App SHALL call `GET /api/pedido` with the JWT token
2. THE Frontend_App SHALL display a table with: pedido `id`, cliente nome, veículo modelo, status, data
3. WHEN user searches, THE Frontend_App SHALL filter the loaded list locally by cliente nome, pedido id, or veículo modelo
4. WHEN user clicks the eye icon, THE Frontend_App SHALL display a modal with full pedido details

> **Status:** ⚠️ Parcialmente possível — `GET /api/pedido` existe. Falta: o `PedidoResponse` não retorna nome do cliente nem modelo do veículo (ver Requisito 9).

---

### Requisito 6: Integração da Página de Clientes do Vendedor

**User Story:** Como vendedor, quero visualizar e gerenciar os clientes cadastrados.

#### Critérios de Aceitação

1. WHEN the page loads, THE Frontend_App SHALL call `GET /api/usuario` and filter users with role `CLIENTE`
2. THE Frontend_App SHALL display nome, email, and role for each client
3. WHEN user clicks "Cadastrar Cliente", THE Frontend_App SHALL call `POST /api/usuario` with nome, email, senhaHash, dataNascimento, role=CLIENTE

> **Status:** ⚠️ Parcialmente possível — `GET /api/usuario` e `POST /api/usuario` existem. Falta o frontend consumir esses endpoints.

---

### Requisito 7: Integração da Página de Administração

**User Story:** Como administrador, quero gerenciar vendedores — criar, ativar e desativar.

#### Critérios de Aceitação

1. WHEN the page loads, THE Frontend_App SHALL call `GET /api/usuario` and filter users with role `VENDEDOR`
2. WHEN admin clicks "Adicionar", THE Frontend_App SHALL call `POST /api/usuario` with role=VENDEDOR
3. WHEN admin confirms deactivation, THE Frontend_App SHALL call `PATCH /api/usuario/{id}/desativar`
4. WHEN admin confirms activation, THE Frontend_App SHALL call `PATCH /api/usuario/{id}/ativar`
5. THE Frontend_App SHALL refresh the list after each operation

> **Status:** ❌ Endpoints `PATCH /api/usuario/{id}/ativar` e `PATCH /api/usuario/{id}/desativar` **não existem no backend** — precisam ser implementados.

---

### Requisito 8: Integração das Páginas de Perfil

**User Story:** Como usuário autenticado, quero ver e editar meus dados de perfil.

#### Critérios de Aceitação

1. WHEN the profile page loads, THE Frontend_App SHALL call `GET /api/usuario/me` with the JWT token
2. THE Frontend_App SHALL display nome, email, and dataNascimento from the response
3. WHEN user clicks "Salvar alterações", THE Frontend_App SHALL call `PUT /api/usuario/{id}` with updated data

> **Status:** ⚠️ Parcialmente possível — `GET /api/usuario/me` e `PUT /api/usuario/{id}` existem. Falta o frontend consumir esses endpoints.

---

### Requisito 9: Enriquecimento do PedidoResponse no Backend

**User Story:** Como vendedor, quero ver o nome do cliente e o modelo do veículo diretamente na listagem de pedidos, sem precisar de chamadas adicionais.

#### Critérios de Aceitação

1. THE Backend_API SHALL include `clienteNome` (String) in `PedidoResponse`
2. THE Backend_API SHALL include `veiculoModelo` (String) in `PedidoResponse`
3. THE Backend_API SHALL include `statusVeiculo` (StatusFabricacao) in `PedidoResponse`

> **Status:** ❌ Não implementado — `PedidoResponse` atual retorna apenas `idCliente` e `idVendedor`, sem nomes ou status.

---

### Requisito 10: Endpoints de Ativação/Desativação de Usuário no Backend

**User Story:** Como administrador, quero ativar e desativar vendedores via API.

#### Critérios de Aceitação

1. THE Backend_API SHALL provide `PATCH /api/usuario/{id}/ativar` that sets user `ativo = true`
2. THE Backend_API SHALL provide `PATCH /api/usuario/{id}/desativar` that sets user `ativo = false`
3. WHEN a deactivated user attempts login, THE Backend_API SHALL return status 401
4. THE Backend_API SHALL require role `ADMIN` to access these endpoints
5. THE Backend_API SHALL return the updated `UsuarioResponse` with status 200

> **Status:** ❌ Não implementado — campo `ativo` e endpoints não existem na entidade `Usuario`.

---

### Requisito 11: Proteção de Rotas no Frontend

**User Story:** Como desenvolvedor, quero proteger rotas baseadas em autenticação e role para que usuários não autorizados não acessem páginas restritas.

#### Critérios de Aceitação

1. WHEN a user without a token accesses any protected route, THE Frontend_App SHALL redirect to `/Login`
2. WHEN role is `CLIENTE`, THE Frontend_App SHALL allow access only to `/Cliente/*` routes
3. WHEN role is `VENDEDOR`, THE Frontend_App SHALL allow access to `/Vendedor/*` routes except `/Vendedor/Administracao`
4. WHEN role is `ADMIN`, THE Frontend_App SHALL allow access to all `/Vendedor/*` routes including `/Vendedor/Administracao`

> **Status:** ❌ Não implementado — atualmente a verificação de role usa apenas `localStorage.getItem("userRole")` sem validação real de token.

---

## Resumo: O que já pode ser integrado

| Funcionalidade | Endpoint Backend | Status |
|---|---|---|
| Login com JWT | `POST /api/usuario/login` | ✅ Pronto para integrar |
| Dados do usuário logado | `GET /api/usuario/me` | ✅ Pronto para integrar |
| Listar todos os usuários | `GET /api/usuario` | ✅ Pronto para integrar |
| Criar usuário | `POST /api/usuario` | ✅ Pronto para integrar |
| Atualizar usuário | `PUT /api/usuario/{id}` | ✅ Pronto para integrar |
| Meus pedidos (cliente) | `GET /api/pedido/meus-pedidos` | ✅ Pronto para integrar |
| Listar todos os pedidos | `GET /api/pedido` | ✅ Pronto para integrar |
| Histórico de status do veículo | `GET /api/veiculo/{id}/status` | ✅ Pronto para integrar |
| Atualizar status do veículo | `POST /api/veiculo/{id}/status` | ✅ Pronto para integrar |

## Resumo: O que falta implementar no Backend

| Funcionalidade | O que falta |
|---|---|
| CORS | Configurar `CorsConfigurationSource` para `localhost:3000` |
| Ativar/desativar usuário | `PATCH /api/usuario/{id}/ativar` e `/desativar` + campo `ativo` na entidade |
| PedidoResponse enriquecido | Adicionar `clienteNome`, `veiculoModelo`, `statusVeiculo` no DTO |
