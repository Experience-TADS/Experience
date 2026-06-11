# Guia de Testes — Postman

> Base URL: `http://localhost:8080`  
> Todas as rotas (exceto login e cadastro) exigem o header `Authorization: Bearer {token}`

---

## Ordem recomendada para teste completo

A sequência abaixo respeita as dependências entre entidades. Siga exatamente essa ordem para evitar erros de referência.

```
── AUTENTICAÇÃO ──────────────────────────────────────────────
 1. POST /api/usuario              → criar usuário ADMIN (id: 1)
 2. POST /api/usuario/login        → obter token JWT do ADMIN

── USUÁRIOS ADICIONAIS ───────────────────────────────────────
 3. POST /api/usuario              → criar usuário VENDEDOR (id: 2)
 4. POST /api/usuario              → criar usuário CLIENTE (id: 3)
 5. POST /api/pessoaFisica         → criar pessoa física (usa dados de usuário)
 6. POST /api/pessoaJuridica       → criar pessoa jurídica (usa dados de usuário)

── DADOS DO USUÁRIO ──────────────────────────────────────────
 7. POST /api/endereco             → criar endereço (idUsuario: 1)
 8. POST /api/telefones            → criar telefone (idUsuario: 1)

── CATÁLOGO ──────────────────────────────────────────────────
 9. POST /api/produto              → criar produto Corolla (id: 1)

── FABRICAÇÃO ────────────────────────────────────────────────
10. POST /api/veiculo              → criar veículo (idProduto: 1)
11. POST /api/veiculo/1/status     → atualizar status para EM_FABRICACAO
12. POST /api/veiculo/1/status     → avançar para CONCLUIDO
13. GET  /api/veiculo/1/status     → verificar histórico completo

── VENDAS ────────────────────────────────────────────────────
14. POST /api/pedido               → criar pedido (idCliente: 3, idVendedor: 2)
15. POST /api/itens-pedido         → adicionar item ao pedido (idProduto: 1)
16. GET  /api/pedido/meus-pedidos  → verificar pedidos do cliente (token do CLIENTE)

── VERIFICAÇÕES FINAIS ───────────────────────────────────────
17. GET  /api/usuario              → listar todos os usuários
18. GET  /api/produto              → listar todos os produtos
19. GET  /api/pedido               → listar todos os pedidos (token ADMIN/VENDEDOR)
```

---

## Fluxo obrigatório antes de qualquer teste

Antes de testar qualquer rota protegida, você precisa de um token JWT. Siga a ordem:

1. Criar um usuário (`POST /api/usuario`)
2. Fazer login (`POST /api/usuario/login`)
3. Copiar o `token` da resposta
4. Em cada requisição protegida, adicionar no header:
   - Key: `Authorization`
   - Value: `Bearer {token_aqui}`

---

## Padrão de cada teste

Cada entidade segue o mesmo padrão de 5 operações:

| Operação | Método | Rota           | Status esperado |
|----------|--------|----------------|-----------------|
| Listar   | GET    | `/api/{rota}`  | 200             |
| Buscar   | GET    | `/api/{rota}/{id}` | 200 / 404   |
| Criar    | POST   | `/api/{rota}`  | 201             |
| Atualizar| PUT    | `/api/{rota}/{id}` | 200 / 404   |
| Deletar  | DELETE | `/api/{rota}/{id}` | 204         |

---

## 1. Usuário

> Comece sempre criando o ADMIN e fazendo login para obter o token. Use esse token em todos os testes seguintes.

### 1.1 Criar usuário ADMIN (primeiro passo — público)
```
POST /api/usuario
Content-Type: application/json
```
```json
{
  "nome": "Admin Master",
  "email": "admin@email.com",
  "senha": "admin123",
  "dataNascimento": "1990-01-01",
  "role": "ADMIN"
}
```
**Resposta esperada:** `201 Created`

---

### 1.2 Login com o ADMIN — obter token
```
POST /api/usuario/login
Content-Type: application/json
```
```json
{
  "email": "admin@email.com",
  "senha": "admin123"
}
```
**Resposta esperada:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@email.com",
  "role": "ADMIN"
}
```
> Copie o `token` e cole em todas as requisições seguintes no header `Authorization: Bearer {token}`.

---

### 1.3 Criar usuário VENDEDOR
```
POST /api/usuario
Content-Type: application/json
```
```json
{
  "nome": "Carlos Vendedor",
  "email": "carlos@email.com",
  "senha": "senha123",
  "dataNascimento": "1988-05-10",
  "role": "VENDEDOR"
}
```
**Resposta esperada:** `201 Created`

---

### 1.4 Criar usuário CLIENTE
```
POST /api/usuario
Content-Type: application/json
```
```json
{
  "nome": "Ana Cliente",
  "email": "ana@email.com",
  "senha": "senha123",
  "dataNascimento": "1995-08-22",
  "role": "CLIENTE"
}
```
**Resposta esperada:** `201 Created`

---

### 1.5 Listar todos os usuários
```
GET /api/usuario
Authorization: Bearer {token}
```
**Resposta esperada:** `200 OK` — array de usuários

---

### 1.6 Buscar usuário por ID
```
GET /api/usuario/1
Authorization: Bearer {token}
```
**Resposta esperada:** `200 OK` ou `404 Not Found`

---

### 1.7 Atualizar usuário
```
PUT /api/usuario/1
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "nome": "Admin Atualizado",
  "email": "admin@email.com",
  "senha": "novaSenha456",
  "dataNascimento": "1990-01-01",
  "role": "ADMIN"
}
```
**Resposta esperada:** `200 OK`

---

### 1.8 Meu perfil (token)
```
GET /api/usuario/me
Authorization: Bearer {token}
```
**Resposta esperada:** `200 OK` — dados do usuário logado

---

### 1.9 Deletar usuário
```
DELETE /api/usuario/3
Authorization: Bearer {token}
```
**Resposta esperada:** `204 No Content`

---

**Roles disponíveis:** `CLIENTE`, `VENDEDOR`, `ADMIN`, `IOT`

---

## 2. Pessoa Física

> Herda de Usuário. Envie os campos do usuário + `cpf`.

### 2.1 Criar Pessoa Física
```
POST /api/pessoaFisica
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "nome": "Maria Souza",
  "email": "maria@email.com",
  "senha": "senha123",
  "dataNascimento": "1990-03-20",
  "role": "CLIENTE",
  "cpf": "52998224725"
}
```
**Resposta esperada:** `200 OK` (retorna a entidade criada)

---

### 2.2 Listar Pessoas Físicas
```
GET /api/pessoaFisica
Authorization: Bearer {token}
```

### 2.3 Buscar por ID
```
GET /api/pessoaFisica/1
Authorization: Bearer {token}
```

### 2.4 Atualizar
```
PUT /api/pessoaFisica/1
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "nome": "Maria Atualizada",
  "email": "maria@email.com",
  "senha": "senha123",
  "dataNascimento": "1990-03-20",
  "role": "CLIENTE",
  "cpf": "52998224725"
}
```

### 2.5 Deletar
```
DELETE /api/pessoaFisica/1
Authorization: Bearer {token}
```
**Resposta esperada:** `204 No Content`

---

## 3. Pessoa Jurídica

### 3.1 Criar Pessoa Jurídica
```
POST /api/pessoaJuridica
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "nome": "Empresa XYZ",
  "email": "contato@xyz.com",
  "senha": "senha123",
  "dataNascimento": "2000-01-01",
  "role": "VENDEDOR",
  "cnpj": "11222333000181",
  "razaoSocial": "XYZ Comércio Ltda"
}
```
**Resposta esperada:** `200 OK`

---

### 3.2 Listar
```
GET /api/pessoaJuridica
Authorization: Bearer {token}
```

### 3.3 Buscar por ID
```
GET /api/pessoaJuridica/1
Authorization: Bearer {token}
```

### 3.4 Atualizar
```
PUT /api/pessoaJuridica/1
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "nome": "Empresa XYZ Atualizada",
  "email": "contato@xyz.com",
  "senha": "senha123",
  "dataNascimento": "2000-01-01",
  "role": "VENDEDOR",
  "cnpj": "11222333000181",
  "razaoSocial": "XYZ Comércio Ltda ME"
}
```

### 3.5 Deletar
```
DELETE /api/pessoaJuridica/1
Authorization: Bearer {token}
```

---

## 4. Endereço

> Requer que o usuário (`idUsuario`) já exista.

### 4.1 Criar Endereço
```
POST /api/endereco
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "cep": "01310100",
  "logradouro": "Avenida Paulista",
  "numero": 1000,
  "bairro": "Bela Vista",
  "cidade": "São Paulo",
  "estado": "SP",
  "idUsuario": 1
}
```
**Resposta esperada:** `201 Created`

---

### 4.2 Listar
```
GET /api/endereco
Authorization: Bearer {token}
```

### 4.3 Buscar por ID
```
GET /api/endereco/1
Authorization: Bearer {token}
```

### 4.4 Atualizar
```
PUT /api/endereco/1
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "cep": "01310100",
  "logradouro": "Avenida Paulista",
  "numero": 2000,
  "bairro": "Bela Vista",
  "cidade": "São Paulo",
  "estado": "SP",
  "idUsuario": 1
}
```

### 4.5 Deletar
```
DELETE /api/endereco/1
Authorization: Bearer {token}
```

---

## 5. Telefone

> Requer que o usuário (`idUsuario`) já exista.

### 5.1 Criar Telefone
```
POST /api/telefones
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "numero": "11999998888",
  "idUsuario": 1
}
```
**Resposta esperada:** `201 Created`

---

### 5.2 Listar
```
GET /api/telefones
Authorization: Bearer {token}
```

### 5.3 Buscar por ID
```
GET /api/telefones/1
Authorization: Bearer {token}
```

### 5.4 Atualizar
```
PUT /api/telefones/1
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "numero": "11988887777",
  "idUsuario": 1
}
```

### 5.5 Deletar
```
DELETE /api/telefones/1
Authorization: Bearer {token}
```

---

## 6. Produto

> Criação e edição requerem role `ADMIN` ou `VENDEDOR`. Leitura requer autenticação.

### 6.1 Criar Produto
```
POST /api/produto
Authorization: Bearer {token} (ADMIN ou VENDEDOR)
Content-Type: application/json
```
```json
{
  "modelo": "Corolla",
  "cor": "Prata",
  "versao": "XEi",
  "ano": 2024
}
```
**Resposta esperada:** `201 Created`

---

### 6.2 Listar
```
GET /api/produto
Authorization: Bearer {token}
```

### 6.3 Buscar por ID
```
GET /api/produto/1
Authorization: Bearer {token}
```

### 6.4 Atualizar
```
PUT /api/produto/1
Authorization: Bearer {token} (ADMIN ou VENDEDOR)
Content-Type: application/json
```
```json
{
  "modelo": "Corolla",
  "cor": "Preto",
  "versao": "Altis Premium",
  "ano": 2025
}
```

### 6.5 Deletar
```
DELETE /api/produto/1
Authorization: Bearer {token} (apenas ADMIN)
```

---

## 7. Veículo

> Requer que o produto (`idProduto`) já exista.

### 7.1 Criar Veículo
```
POST /api/veiculo
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "idProduto": 1,
  "chassi": 123456789,
  "statusVeiculo": "AGUARDANDO"
}
```
**Status disponíveis:** `AGUARDANDO`, `EM_FABRICACAO`, `PINTURA`, `CONTROLE_QUALIDADE`, `CONCLUIDO`, `ENTREGUE`, `CANCELADO`  
**Resposta esperada:** `201 Created`

---

### 7.2 Listar
```
GET /api/veiculo
Authorization: Bearer {token}
```

### 7.3 Buscar por ID
```
GET /api/veiculo/1
Authorization: Bearer {token}
```

### 7.4 Atualizar
```
PUT /api/veiculo/1
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "idProduto": 1,
  "chassi": 123456789,
  "statusVeiculo": "EM_FABRICACAO"
}
```

### 7.5 Deletar
```
DELETE /api/veiculo/1
Authorization: Bearer {token}
```

---

## 8. Status Histórico do Veículo

> Leitura: qualquer autenticado. Escrita: apenas `ADMIN` ou `IOT`.

### 8.1 Listar histórico de um veículo
```
GET /api/veiculo/1/status
Authorization: Bearer {token}
```
**Resposta esperada:** `200 OK` — lista de status históricos

---

### 8.2 Atualizar status do veículo
```
POST /api/veiculo/1/status
Authorization: Bearer {token} (ADMIN ou IOT)
Content-Type: application/json
```
```json
"EM_FABRICACAO"
```
> Atenção: o body é uma string simples (enum), não um objeto JSON.

**Resposta esperada:** `200 OK`

---

## 9. Pedido

> Requer role `VENDEDOR` ou `ADMIN`. Clientes só acessam `/meus-pedidos`.

### 9.1 Criar Pedido
```
POST /api/pedido
Authorization: Bearer {token} (VENDEDOR ou ADMIN)
Content-Type: application/json
```
```json
{
  "idCliente": 1,
  "idVendedor": 2,
  "dataPedido": "2026-04-27T10:00:00",
  "valorTotal": 150000.00
}
```
**Resposta esperada:** `201 Created`

---

### 9.2 Listar todos os pedidos
```
GET /api/pedido
Authorization: Bearer {token} (VENDEDOR ou ADMIN)
```

### 9.3 Buscar pedido por ID
```
GET /api/pedido/1
Authorization: Bearer {token} (VENDEDOR ou ADMIN)
```

### 9.4 Meus pedidos (cliente)
```
GET /api/pedido/meus-pedidos
Authorization: Bearer {token} (CLIENTE, VENDEDOR ou ADMIN)
```
**Resposta esperada:** `200 OK` — pedidos do usuário logado

### 9.5 Atualizar Pedido
```
PUT /api/pedido/1
Authorization: Bearer {token} (VENDEDOR ou ADMIN)
Content-Type: application/json
```
```json
{
  "idCliente": 1,
  "idVendedor": 2,
  "dataPedido": "2026-04-27T10:00:00",
  "valorTotal": 160000.00
}
```

### 9.6 Deletar Pedido
```
DELETE /api/pedido/1
Authorization: Bearer {token} (VENDEDOR ou ADMIN)
```

---

## 10. Item de Pedido

> Requer que o produto (`idProduto`) já exista.

### 10.1 Criar Item de Pedido
```
POST /api/itens-pedido
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "idProduto": 1,
  "quantidade": 2
}
```
**Resposta esperada:** `201 Created`

---

### 10.2 Listar
```
GET /api/itens-pedido
Authorization: Bearer {token}
```

### 10.3 Buscar por ID
```
GET /api/itens-pedido/1
Authorization: Bearer {token}
```

### 10.4 Atualizar
```
PUT /api/itens-pedido/1
Authorization: Bearer {token}
Content-Type: application/json
```
```json
{
  "idProduto": 1,
  "quantidade": 5
}
```

### 10.5 Deletar
```
DELETE /api/itens-pedido/1
Authorization: Bearer {token}
```

---

