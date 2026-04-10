# POSTs - Experience

Base URL: `http://localhost:8080`

---

## 1. Usuário

```
POST /api/usuario
```
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senhaHash": "senha123",
  "dataNascimento": "1995-06-15"
}
```

---

## 2. Login

```
POST /api/usuario/login
```
```json
{
  "email": "joao@email.com",
  "senha": "senha123"
}
```

---

## 3. Pessoa Física

```
POST /api/pessoaFisica
```
```json
{
  "nome": "Maria Souza",
  "email": "maria@email.com",
  "senhaHash": "senha123",
  "dataNascimento": "1990-03-20",
  "cpf": "52998224725"
}
```
> CPF válido para testes: `52998224725`

---

## 4. Pessoa Jurídica

```
POST /api/pessoaJuridica
```
```json
{
  "nome": "Empresa Teste",
  "email": "empresa@email.com",
  "senhaHash": "senha123",
  "dataNascimento": "2000-01-01",
  "cnpj": "11222333000181",
  "razaoSocial": "Empresa Teste Ltda"
}
```
> CNPJ válido para testes: `11222333000181`

---

## 5. Produto

```
POST /api/produto
```
```json
{
  "modelo": "Toyota Corola",
  "cor": "Preto",
  "versao": "Altis",
  "ano": 2026
}
```

---

## 6. Endereço

```
POST /api/endereco
```
```json
{
  "id_usuario": "1",
  "cep": "01310100",
  "logradouro": "Avenida Paulista",
  "numero": 1000,
  "bairro": "Bela Vista",
  "cidade": "São Paulo",
  "estado": "SP"
}
```

---

## 7. Telefone

```
POST /api/telefones
```
```json
{
    "id_usuario": "1",
    "numero": "11987654321"
}
```
> 10 dígitos (fixo) ou 11 dígitos (celular com 9), apenas números.
