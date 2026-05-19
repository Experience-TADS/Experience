# Plano de Implementação: Integração Frontend-Backend Toyota Experience

## Visão Geral

Integração completa entre o frontend Next.js 14 e o backend Spring Boot, substituindo todos os dados mockados por chamadas reais à API REST, implementando autenticação JWT e proteção de rotas por role.

## Tasks

---

### BACKEND

- [ ] 1. Configurar CORS no backend
  - [ ] 1.1 Criar `CorsConfig.java` em `experience/src/main/java/com/senai/experience/security/`
    - Criar classe `@Configuration` com bean `CorsConfigurationSource`
    - Permitir origem `http://localhost:3000`
    - Permitir métodos: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`
    - Permitir headers: `Authorization`, `Content-Type`, `Accept`
    - Definir `allowCredentials(true)`
    - Registrar configuração para o padrão `/**`
    - _Requisitos: 1.1, 1.2, 1.3, 1.4_
  - [ ] 1.2 Registrar `CorsConfigurationSource` no `SecurityConfig.java`
    - Injetar o bean `CorsConfigurationSource` no `SecurityConfig`
    - Chamar `http.cors(cors -> cors.configurationSource(...))` na cadeia de filtros
    - _Requisitos: 1.1, 1.4_

- [ ] 2. Adicionar campo `ativo` e endpoints de ativação/desativação no backend
  - [ ] 2.1 Adicionar campo `ativo` na entidade `Usuario.java`
    - Adicionar `@Column(nullable = false) private boolean ativo = true;` em `experience/src/main/java/com/senai/experience/entities/Usuario.java`
    - Adicionar getter `isAtivo()` e setter `setAtivo(boolean ativo)` explícitos (Lombok não está funcionando corretamente no projeto)
    - _Requisitos: 10.1, 10.2_
  - [ ] 2.2 Expor campo `ativo` no `UsuarioResponse.java`
    - Adicionar `private boolean ativo;` em `experience/src/main/java/com/senai/experience/DTO/response/UsuarioResponse.java`
    - Adicionar getter/setter para o campo
    - _Requisitos: 10.5_
  - [ ] 2.3 Atualizar `UsuarioMapper.java` para mapear o campo `ativo`
    - Em `experience/src/main/java/com/senai/experience/mappers/UsuarioMapper.java`, incluir `r.setAtivo(u.isAtivo())` no método `toResponse`
    - _Requisitos: 10.5_
  - [ ] 2.4 Adicionar métodos `ativar` e `desativar` no `UsuarioService.java`
    - Em `experience/src/main/java/com/senai/experience/services/UsuarioService.java`, adicionar:
      - `public Usuario ativar(Long id)` — busca usuário, seta `ativo = true`, salva e retorna
      - `public Usuario desativar(Long id)` — busca usuário, seta `ativo = false`, salva e retorna
    - _Requisitos: 10.1, 10.2_
  - [ ] 2.5 Adicionar endpoints `PATCH /{id}/ativar` e `PATCH /{id}/desativar` no `UsuarioController.java`
    - Em `experience/src/main/java/com/senai/experience/controllers/UsuarioController.java`, adicionar:
      - `@PatchMapping("/{id}/ativar")` que chama `usuarioService.ativar(id)` e retorna `UsuarioResponse` com status 200
      - `@PatchMapping("/{id}/desativar")` que chama `usuarioService.desativar(id)` e retorna `UsuarioResponse` com status 200
      - Retornar 404 se o usuário não for encontrado
    - _Requisitos: 10.1, 10.2, 10.4, 10.5_
  - [ ] 2.6 Verificar campo `ativo` no login em `UsuarioService.java`
    - No método `login`, após validar email e senha, verificar se `usuario.isAtivo()` é `true`
    - Se `ativo = false`, retornar `null` (o controller já retorna 401 para null)
    - _Requisitos: 10.3_

- [ ] 3. Enriquecer `PedidoResponse` com dados do cliente e veículo
  - [ ] 3.1 Adicionar campos `clienteNome`, `veiculoModelo` e `statusVeiculo` no `PedidoResponse.java`
    - Em `experience/src/main/java/com/senai/experience/DTO/response/PedidoResponse.java`, adicionar:
      - `private String clienteNome;`
      - `private String veiculoModelo;`
      - `private StatusFabricacao statusVeiculo;`
    - Adicionar getters/setters para os três campos
    - _Requisitos: 9.1, 9.2, 9.3_
  - [ ] 3.2 Atualizar `PedidoMapper.java` para buscar e mapear os campos enriquecidos
    - Em `experience/src/main/java/com/senai/experience/mappers/PedidoMapper.java`, atualizar `toResponse` para:
      - Buscar o nome do cliente via `p.getIdCliente().getNome()` (se não nulo)
      - Buscar o modelo do veículo via `p.getVeiculo()` → `veiculo.getProduto().getModelo()` (se não nulo)
      - Buscar o status do veículo via `veiculo.getStatusVeiculo()` (se não nulo)
      - Setar `clienteNome`, `veiculoModelo` e `statusVeiculo` no response
    - Verificar se a entidade `Pedido.java` possui relacionamento com `Veiculo`; se não, adicionar `@ManyToOne` com `@JoinColumn` em `experience/src/main/java/com/senai/experience/entities/Pedido.java`
    - _Requisitos: 9.1, 9.2, 9.3_

- [ ] 4. Checkpoint Backend — Verificar compilação e endpoints
  - Garantir que o projeto compila sem erros (`mvn compile` em `experience/`)
  - Verificar que os três endpoints novos respondem: `PATCH /api/usuario/{id}/ativar`, `PATCH /api/usuario/{id}/desativar`, e que `GET /api/pedido` retorna `clienteNome` e `veiculoModelo`
  - Verificar que `POST /api/usuario/login` retorna 401 para usuário com `ativo = false`
  - Perguntar ao usuário se há dúvidas antes de prosseguir para o frontend

---

### FRONTEND — Camada de Tipos e Serviços

- [ ] 5. Criar `lib/types.ts` com interfaces TypeScript
  - Criar o arquivo `lib/types.ts` na raiz do projeto Next.js
  - Definir e exportar:
    - `type UserRole = 'CLIENTE' | 'VENDEDOR' | 'ADMIN' | 'IOT'`
    - `type StatusFabricacao` com os 7 valores do enum do backend
    - `interface LoginRequest { email: string; senha: string }`
    - `interface LoginResponse { token: string; email: string; role: UserRole }`
    - `interface UsuarioResponse { id: number; nome: string; email: string; dataNascimento: string; role: UserRole; ativo?: boolean }`
    - `interface UsuarioRequest { nome: string; email: string; senhaHash: string; dataNascimento: string; role: UserRole }`
    - `interface PedidoResponse { id: number; idCliente: number; idVendedor: number; dataPedido: string; valorTotal: number; clienteNome?: string; veiculoModelo?: string; statusVeiculo?: StatusFabricacao }`
    - `interface VeiculoResponse { id: number; idProduto: number; chassi: number; statusVeiculo: StatusFabricacao }`
    - `interface StatusHistoricoResponse { id: number; veiculoId: number; status: StatusFabricacao; dataAtualizacao: string }`
    - `const STATUS_LABELS: Record<StatusFabricacao, string>` com os 7 mapeamentos PT-BR definidos no design
  - _Requisitos: 2.4, 4.4_

- [ ] 6. Criar `lib/auth.ts` com `AuthService`
  - Criar o arquivo `lib/auth.ts` na raiz do projeto Next.js
  - Implementar e exportar o objeto `AuthService` com os métodos:
    - `getToken(): string | null` — lê `localStorage.getItem('token')`
    - `getRole(): UserRole | null` — lê `localStorage.getItem('userRole')` e faz cast para `UserRole`
    - `getEmail(): string | null` — lê `localStorage.getItem('userEmail')`
    - `getUserId(): number | null` — lê `localStorage.getItem('userId')` e converte para número
    - `setSession(data: LoginResponse): void` — armazena `token`, `userRole` e `userEmail` no `localStorage`
    - `clearSession(): void` — remove `token`, `userRole`, `userEmail` e `userId` do `localStorage`
    - `isAuthenticated(): boolean` — retorna `true` se `getToken()` não for nulo e não vazio
  - Todos os métodos devem verificar `typeof window !== 'undefined'` antes de acessar `localStorage`
  - _Requisitos: 3.2, 3.3, 11.1_

- [ ] 7. Criar `lib/api.ts` com API Client
  - Criar o arquivo `lib/api.ts` na raiz do projeto Next.js
  - Implementar a função `request<T>` que:
    - Lê `NEXT_PUBLIC_API_URL` com fallback para `http://localhost:8080`
    - Injeta `Authorization: Bearer {token}` quando token existe (via `AuthService.getToken()`)
    - Em resposta 401: chama `AuthService.clearSession()` e redireciona para `/Login`
    - Em resposta não-ok: lança `Error` com o status HTTP
  - Implementar e exportar os métodos agrupados por domínio:
    - **auth**: `login(data: LoginRequest): Promise<LoginResponse>`
    - **usuario**: `getMe()`, `getUsuarios()`, `createUsuario(data: UsuarioRequest)`, `updateUsuario(id, data)`, `deleteUsuario(id)`, `ativarUsuario(id)`, `desativarUsuario(id)`
    - **pedido**: `getPedidos()`, `getMeusPedidos()`, `getPedidoById(id)`, `createPedido(data)`
    - **veiculo**: `getVeiculos()`, `getVeiculoById(id)`, `getStatusHistorico(veiculoId)`, `updateStatusVeiculo(veiculoId, status)`
  - Todos os métodos devem usar os tipos de `lib/types.ts`
  - _Requisitos: 2.1, 2.2, 2.3, 2.4, 2.5_

- [ ] 8. Criar `middleware.ts` com proteção de rotas por role
  - Criar o arquivo `middleware.ts` na raiz do projeto Next.js (ao lado de `app/`)
  - Implementar a lógica de proteção:
    - Ler o token de `localStorage` via cookie ou header (middleware roda no edge — usar `request.cookies.get('token')` ou verificar header `Authorization`)
    - Se não houver token e a rota for `/Cliente/*` ou `/Vendedor/*`: redirecionar para `/Login`
    - Se role for `CLIENTE` e a rota não começar com `/Cliente/`: redirecionar para `/Cliente/Acompanhamento`
    - Se role for `VENDEDOR` e a rota for `/Vendedor/Administracao`: redirecionar para `/Vendedor/Dashbord`
    - Se role for `ADMIN`: permitir acesso a todas as rotas `/Vendedor/*`
  - Exportar `config` com `matcher: ['/Cliente/:path*', '/Vendedor/:path*']`
  - Ajustar `lib/auth.ts` e `lib/api.ts` para também salvar o token em cookie (além de `localStorage`) para que o middleware consiga lê-lo
  - _Requisitos: 11.1, 11.2, 11.3, 11.4_

---

### FRONTEND — Páginas

- [ ] 9. Corrigir `app/Login/page.tsx` — tela de login real
  - Substituir o conteúdo atual de `app/Login/page.tsx` (que contém a tela de Administração por engano) por uma tela de login real com:
    - Formulário com campos `email` (type="email") e `senha` (type="password")
    - Estado `loading` que desabilita o botão e exibe spinner durante a requisição
    - Estado `erro` que exibe a mensagem "Email ou senha inválidos" abaixo do formulário
    - Ao submeter: chamar `api.login({ email, senha })`
    - Em sucesso: chamar `AuthService.setSession(response)` e redirecionar por role:
      - `CLIENTE` → `/Cliente/Acompanhamento`
      - `VENDEDOR` → `/Vendedor/Dashbord`
      - `ADMIN` → `/Vendedor/Administracao`
    - Em erro 401: exibir mensagem de erro
  - _Requisitos: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8_

- [ ] 10. Integrar `app/Cliente/Acompanhamento/page.tsx`
  - Substituir o array `pedidos` mockado por chamada real à API:
    - No `useEffect`, chamar `api.getMeusPedidos()` e armazenar no estado
    - Exibir estado de loading enquanto a requisição está em andamento
    - Exibir mensagem "Erro ao carregar pedidos" se a chamada falhar
  - Ao expandir um pedido, chamar `api.getStatusHistorico(veiculoId)` para buscar o histórico de status
    - Mapear os valores de `StatusFabricacao` para labels PT-BR usando `STATUS_LABELS` de `lib/types.ts`
    - Exibir as etapas do histórico na timeline existente
  - Usar `PedidoResponse.statusVeiculo` para determinar a etapa atual exibida no card
  - _Requisitos: 4.1, 4.2, 4.3, 4.4, 4.5_

- [ ] 11. Integrar `app/Vendedor/Pedidos/page.tsx`
  - Substituir o array `pedidosData` mockado por chamada real à API:
    - No `useEffect`, chamar `api.getPedidos()` e armazenar no estado
    - Exibir estado de loading enquanto a requisição está em andamento
  - Atualizar a tabela para usar os campos do `PedidoResponse`:
    - Coluna "Cliente": usar `pedido.clienteNome`
    - Coluna "Veículo": usar `pedido.veiculoModelo`
    - Coluna "Status": usar `STATUS_LABELS[pedido.statusVeiculo]` de `lib/types.ts`
    - Coluna "Data": formatar `pedido.dataPedido`
  - Manter a filtragem local existente, adaptando para filtrar por `clienteNome`, `id` e `veiculoModelo`
  - _Requisitos: 5.1, 5.2, 5.3, 5.4_

- [ ] 12. Integrar `app/Vendedor/Clientes/page.tsx`
  - Substituir o array `clientes` mockado por chamada real à API:
    - No `useEffect`, chamar `api.getUsuarios()`, filtrar por `role === 'CLIENTE'` e armazenar no estado
    - Exibir estado de loading enquanto a requisição está em andamento
  - Atualizar a função `cadastrarCliente` para chamar `api.createUsuario({ ...novoCliente, role: 'CLIENTE' })`
    - Após sucesso, recarregar a lista chamando `api.getUsuarios()` novamente
    - Exibir mensagem de erro se a criação falhar
  - Adaptar os campos exibidos na tabela para usar `UsuarioResponse` (`nome`, `email`, `role`)
  - _Requisitos: 6.1, 6.2, 6.3_

- [ ] 13. Integrar `app/Vendedor/Administracao/page.tsx`
  - Substituir o array `vendedores` mockado e a verificação de role via `localStorage` por chamadas reais:
    - No `useEffect`, chamar `api.getUsuarios()`, filtrar por `role === 'VENDEDOR'` e armazenar no estado
    - Remover a lógica de `status === 'blocked'` baseada em `localStorage.getItem('userRole')` — a proteção agora é feita pelo `middleware.ts`
  - Atualizar a função `adicionar` para chamar `api.createUsuario({ nome, email, senhaHash: '', dataNascimento: '', role: 'VENDEDOR' })`
    - Após sucesso, recarregar a lista
  - Atualizar a função `confirmarToggle` para chamar `api.ativarUsuario(id)` ou `api.desativarUsuario(id)` conforme o estado atual do vendedor
    - Após sucesso, recarregar a lista
  - _Requisitos: 7.1, 7.2, 7.3, 7.4, 7.5_

- [ ] 14. Integrar páginas de Perfil (`app/Cliente/perfil/page.tsx` e `app/Vendedor/Perfil/page.tsx`)
  - **`app/Cliente/perfil/page.tsx`**:
    - No `useEffect`, chamar `api.getMe()` e preencher os campos `nome`, `email` e `dataNascimento` com os dados retornados
    - Armazenar o `id` do usuário no estado para usar no PUT
    - Substituir o nome hardcoded "Lauren" pelo `nome` retornado pela API
    - Exibir `dataNascimento` formatada
  - **`app/Vendedor/Perfil/page.tsx`**:
    - No `useEffect`, chamar `api.getMe()` e preencher o formulário com `nome`, `email` e `dataNascimento`
    - Armazenar o `id` do usuário no estado
    - Substituir os dados hardcoded ("Ricardo Mendes", "ricardo.mendes@toyota.com") pelos dados da API
    - Ao clicar em "Salvar alterações", chamar `api.updateUsuario(id, { nome, email, senhaHash: '', dataNascimento, role })` e exibir feedback de sucesso ou erro
  - _Requisitos: 8.1, 8.2, 8.3_

- [ ] 15. Checkpoint Frontend — Verificar integração end-to-end
  - Garantir que não há erros de TypeScript (`tsc --noEmit`)
  - Verificar que o fluxo de login → redirecionamento por role funciona
  - Verificar que páginas protegidas redirecionam para `/Login` sem token
  - Perguntar ao usuário se há dúvidas antes de prosseguir para os testes

---

### TESTES DE PROPRIEDADE

- [ ] 16. Implementar testes de propriedade com fast-check
  - Instalar `fast-check` como dependência de desenvolvimento: `npm install --save-dev fast-check`
  - Criar o arquivo de testes em `lib/__tests__/properties.test.ts`
  - [ ]* 16.1 Escrever teste de propriedade para inclusão do token JWT no header
    - **Propriedade 1: Token JWT sempre incluído nas requisições autenticadas**
    - Para qualquer string de token não vazia, verificar que `getHeaders()` retorna `Authorization: Bearer {token}` com o valor exato
    - **Valida: Requisito 2.3**
  - [ ]* 16.2 Escrever teste de propriedade para mapeamento completo de `StatusFabricacao`
    - **Propriedade 3: Mapeamento completo de StatusFabricacao**
    - Para qualquer valor do enum `StatusFabricacao`, verificar que `STATUS_LABELS[status]` retorna string não vazia e diferente do valor original
    - **Valida: Requisito 4.4**
  - [ ]* 16.3 Escrever teste de propriedade para filtragem local de pedidos
    - **Propriedade 4: Filtragem local de pedidos preserva relevância**
    - Para qualquer string de busca e qualquer lista de pedidos, verificar que todos os itens retornados pelo filtro contêm a string em `clienteNome`, `id` ou `veiculoModelo` (case-insensitive)
    - **Valida: Requisito 5.3**
  - [ ]* 16.4 Escrever teste de propriedade para proteção de rotas por role
    - **Propriedade 5 e 6: Proteção de rotas e controle de acesso por role**
    - Para qualquer rota `/Cliente/*` ou `/Vendedor/*` sem token: verificar redirecionamento para `/Login`
    - Para role `CLIENTE` em rota fora de `/Cliente/*`: verificar redirecionamento
    - Para role `VENDEDOR` em `/Vendedor/Administracao`: verificar redirecionamento
    - Para role `ADMIN` em qualquer rota `/Vendedor/*`: verificar acesso permitido
    - **Valida: Requisitos 11.1, 11.2, 11.3, 11.4**
  - [ ]* 16.5 Escrever teste de propriedade para idempotência de ativar/desativar
    - **Propriedade 7: Ativar/desativar usuário é idempotente no estado final**
    - Simular chamadas múltiplas a `ativarUsuario` e verificar que o estado final é sempre `ativo = true`
    - Simular chamadas múltiplas a `desativarUsuario` e verificar que o estado final é sempre `ativo = false`
    - **Valida: Requisitos 10.1, 10.2, 10.5**

---

## Notas

- Tasks marcadas com `*` são opcionais e podem ser puladas para um MVP mais rápido
- Cada task referencia os requisitos específicos do `requirements.md` para rastreabilidade
- O middleware do Next.js roda no edge e não tem acesso ao `localStorage` — o token deve ser salvo também em cookie (httpOnly não é possível via JS, usar cookie regular) para que o middleware consiga lê-lo
- O campo `ativo` na entidade `Usuario` requer uma migration de banco de dados se o banco já tiver dados; o valor padrão `true` garante compatibilidade retroativa
- O `PedidoMapper` precisa de acesso ao `Veiculo` associado ao pedido — verificar se a entidade `Pedido` já possui esse relacionamento antes de implementar a task 3.2
