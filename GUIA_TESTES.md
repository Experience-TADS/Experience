# Guia de Testes — Toyota Experience

## Pré-requisito

Suba todos os serviços a partir da raiz do projeto:

```bash
docker compose up --build
```

Aguarde todos os containers ficarem healthy (especialmente `experience-postgres` e `experience-api`).

---

## Serviços disponíveis

| Serviço        | URL                          |
|----------------|------------------------------|
| Frontend       | http://localhost:3000        |
| API REST       | http://localhost:8080        |
| Swagger        | http://localhost:8080/swagger-ui.html |
| Chatbot Flask  | http://localhost:5000        |
| Grafana        | http://localhost:3001        |
| Node-RED       | http://localhost:1880        |
| InfluxDB       | http://localhost:8086        |
| pgAdmin        | http://localhost:5051        |

---

## Credenciais de acesso

### Admin
| Campo | Valor |
|-------|-------|
| E-mail | `admin@experience.com` |
| Senha  | `admin123` |

### Vendedor
| Campo | Valor |
|-------|-------|
| E-mail | `vendedor.toyota@experience.com` |
| Senha  | `vendedor123` |

### Clientes (todos com senha `cliente123`)
| Nome              | E-mail                        |
|-------------------|-------------------------------|
| Ana Souza         | ana.souza@email.com           |
| Bruno Lima        | bruno.lima@email.com          |
| Carla Mendes      | carla.mendes@email.com        |
| Diego Ferreira    | diego.ferreira@email.com      |
| Elisa Costa       | elisa.costa@email.com         |
| Felipe Rocha      | felipe.rocha@email.com        |
| Gabriela Nunes    | gabriela.nunes@email.com      |
| Henrique Alves    | henrique.alves@email.com      |
| Isabela Martins   | isabela.martins@email.com     |
| Joao Pereira      | joao.pereira@email.com        |

### pgAdmin
| Campo | Valor |
|-------|-------|
| E-mail | `admin@experience.com` |
| Senha  | `admin` |

### Grafana / InfluxDB
| Campo | Valor |
|-------|-------|
| Usuário | `admin` |
| Senha   | `admin123` |

---

## Fluxo de teste

### 1. Login como Vendedor
1. Acesse http://localhost:3000
2. Faça login com `vendedor.toyota@experience.com` / `vendedor123`
3. Você verá o painel do vendedor com acesso a Pedidos, Clientes, Dashboard e Administração

### 2. Login como Cliente
1. Acesse http://localhost:3000
2. Faça login com qualquer cliente da tabela acima (ex: `ana.souza@email.com` / `cliente123`)
3. O cliente verá o acompanhamento do próprio pedido e veículo

### 3. Acompanhamento de fabricação
- Cada cliente possui **1 pedido** e **1 veículo** vinculado, iniciado no status `AGUARDANDO`
- O status evolui conforme as etapas abaixo (alimentadas pelo simulador MQTT):

```
AGUARDANDO → MONTAGEM_ESTRUTURAL → PINTURA → INSTALACAO_MOTOR
          → ACABAMENTO_INTERNO → INSPECAO_FINAL → LIBERACAO_TRANSPORTE → ENTREGUE
```

### 4. Chatbot (Totoya)
- Disponível na tela do cliente
- Acesse também direto em http://localhost:5000/api/start
- Suporta consulta de códigos OBD2 (ex: `P0300`, `C1201`) e menu de atendimento

### 5. Dados mockados no banco
O banco é populado automaticamente na primeira inicialização da API com:
- **1 admin**, **1 vendedor**, **10 clientes**
- **5 produtos**: Corolla XEi, Hilux SRX, Yaris XLS, RAV4 GR-S, SW4 Diamond
- **10 pedidos** e **10 veículos** no status `AGUARDANDO`

### 6. Simulador MQTT
O container `experience-simulador` sobe automaticamente e publica eventos de fabricação no tópico `senai/pii_toy`. Para acompanhar no Node-RED, acesse http://localhost:1880.

---

## Observações
- O banco **não é resetado** entre restarts — os dados persistem no volume `postgres_data`
- Para resetar tudo: `docker compose down -v` e depois `docker compose up --build`
