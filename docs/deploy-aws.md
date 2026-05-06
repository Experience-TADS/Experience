# Relatório de Deploy — Toyota Experience
## AWS (Backend + Banco) + Vercel (Frontend)

**Data:** 28 de Abril de 2026  
**Projeto:** Toyota Experience — Projeto Integrador SENAI  
**Responsável:** Lauren Reis

---

## Arquitetura Final

```
[Usuário]
    │
    ▼
[Vercel] ──── Frontend Next.js
    │          experience-n2t08q151-lauren-reis-projects.vercel.app
    │
    ▼
[AWS EC2] ──── Backend Spring Boot
    │           http://54.91.247.129 (porta 80)
    │
    ▼
[AWS RDS] ──── PostgreSQL
                toyota-experience-db.ciz6s4a0gwps.us-east-1.rds.amazonaws.com:5432
```

---

## Recursos AWS Criados

| Recurso | Nome | Detalhes |
|---------|------|----------|
| RDS | toyota-experience-db | PostgreSQL 18.3, db.t4g.micro, us-east-1b |
| EC2 | toyota-experience-backend | t3.micro, Amazon Linux 2023, us-east-1 |
| Security Group (RDS) | default | sg-025f7691bd6146118 |
| Security Group (EC2) | launch-wizard-2 | sg-069ce495b126fe582 |

---

## PASSO 1 — Banco de Dados (AWS RDS)

### O que foi feito
- Criado banco PostgreSQL no RDS com as configurações:
  - Engine: PostgreSQL 18.3
  - Instância: db.t4g.micro (nível gratuito)
  - Identificador: `toyota-experience-db`
  - Usuário: `postgres`
  - Banco padrão: `postgres`
  - Região: us-east-1b

### Endpoint gerado
```
toyota-experience-db.ciz6s4a0gwps.us-east-1.rds.amazonaws.com
```

### Configuração do Security Group
- Grupo: `default` (sg-025f7691bd6146118)
- Regra adicionada: PostgreSQL, porta 5432, origem 0.0.0.0/0

---

## PASSO 2 — Instância EC2

### O que foi feito
- Criada instância EC2 com:
  - AMI: Amazon Linux 2023 (ami-098e39bafa7e7303d)
  - Tipo: t3.micro
  - Par de chaves: `toyota-experience-key.pem`
  - IP público: 54.91.247.129 (muda a cada reinicialização)
  - Security Group: launch-wizard-2

### Regras do Security Group (launch-wizard-2)
| Porta | Protocolo | Origem | Finalidade |
|-------|-----------|--------|------------|
| 22 | TCP | 0.0.0.0/0 | SSH |
| 80 | TCP | 0.0.0.0/0 | HTTP (backend) |
| 8080 | TCP | 0.0.0.0/0 | Porta alternativa |

### ⚠️ Atenção
O IP público da EC2 **muda toda vez que a instância é reiniciada**. Para um IP fixo, associar um **Elastic IP**.

---

## PASSO 3 — Configuração do Backend na EC2

### Conexão
Feita via **EC2 Instance Connect** (browser) — sem necessidade do arquivo .pem.

### Instalação do Java e Maven
```bash
sudo dnf update -y && sudo dnf install -y java-17-amazon-corretto git maven
```

### Clone do repositório
```bash
git clone https://github.com/Experience-TADS/Experience.git
cd Experience/experience
```

### Erros encontrados e soluções

#### Erro 1 — Permission denied no mvnw
```
-bash: ./mvnw: Permission denied
```
**Solução:**
```bash
chmod +x mvnw
```

#### Erro 2 — Caracteres invisíveis ao colar comandos
O terminal do EC2 Instance Connect no browser não aceita Ctrl+C/Ctrl+V normalmente.  
**Solução:** Usar botão direito do mouse para colar.

#### Erro 3 — Arquivo de serviço duplicado no nano
Ao colar o conteúdo do arquivo `.service` no nano, o conteúdo foi duplicado.  
**Solução:** Deletar o arquivo e recriar com `sudo nano`, colando com botão direito apenas uma vez.

#### Erro 4 — Porta 8080 não acessível pelo browser
**Causa:** Redes móveis e alguns browsers bloqueiam portas não padrão.  
**Solução:** Mudar o Spring Boot para rodar na porta 80 com `User=root` no serviço systemd.

#### Erro 5 — `sudo tee`, `authbind`, `nft` não encontrados
**Solução:** Usar `sudo nano` para editar arquivos diretamente.

### Build do projeto
```bash
chmod +x mvnw && ./mvnw clean package -DskipTests
```
**Resultado:** BUILD SUCCESS em 24.5 segundos.

### Arquivo de serviço systemd
Criado em `/etc/systemd/system/toyota-experience.service`:

```ini
[Unit]
Description=Toyota Experience Backend
After=network.target

[Service]
User=root
WorkingDirectory=/home/ec2-user/Experience/experience
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_URL=jdbc:postgresql://toyota-experience-db.ciz6s4a0gwps.us-east-1.rds.amazonaws.com:5432/postgres"
Environment="DB_USERNAME=postgres"
Environment="DB_PASSWORD=Toyota2026ADS"
Environment="JWT_SECRET=dGhpc2lzYXZlcnlsb25nc2VjcmV0a2V5Zm9yand0dG9rZW5zMTIzNDU2Nzg="
Environment="MAIL_USERNAME="
Environment="MAIL_PASSWORD="
Environment="FRONTEND_URL=*"
ExecStart=/usr/bin/java -Dserver.port=80 -jar /home/ec2-user/Experience/experience/target/experience-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### Inicialização do serviço
```bash
sudo systemctl daemon-reload
sudo systemctl enable toyota-experience
sudo systemctl start toyota-experience
```

### URL do backend
```
http://54.91.247.129
```

---

## PASSO 4 — Frontend (Vercel)

### Configuração
- Repositório: Experience-TADS/Experience
- Branch: feature/FrontEndWeb (via Deploy Hook)
- Framework: Next.js
- Root Directory: (vazio)

### Erros encontrados e soluções

#### Erro 1 — 404 no primeiro deploy
Branch `main` não contém o frontend.  
**Solução:** Criar Deploy Hook apontando para `feature/FrontEndWeb`.

#### Erro 2 — Framework Preset "Other"
**Solução:** Alterar em Settings → Build and Deployment → Framework Preset → Next.js.

### URL do frontend
```
https://experience-n2t08q151-lauren-reis-projects.vercel.app
```

---

## Status Final

| Componente | Status | URL |
|------------|--------|-----|
| 🗄️ Banco RDS PostgreSQL | ✅ Disponível | `toyota-experience-db.ciz6s4a0gwps.us-east-1.rds.amazonaws.com:5432` |
| ⚙️ Backend Spring Boot | ✅ Rodando | `http://54.91.247.129` |
| 🌐 Frontend Next.js | ✅ Rodando | `https://experience-n2t08q151-lauren-reis-projects.vercel.app` |

---

# Tutorial — Como Subir Atualizações Futuras

## Atualizar o Backend

**1. Conectar na EC2**
- AWS Console → EC2 → Instâncias → verificar novo IP público
- Conectar → EC2 Instance Connect → Conectar

**2. Puxar atualizações**
```bash
cd Experience/experience
git pull origin main
```

**3. Recompilar**
```bash
./mvnw clean package -DskipTests
```

**4. Reiniciar o serviço**
```bash
sudo systemctl restart toyota-experience
```

**5. Verificar**
```bash
sudo systemctl status toyota-experience
```

---

## Atualizar o Frontend

**Opção 1 — Push na branch** (automático pela Vercel)

**Opção 2 — Deploy Hook manual**
- Vercel → Settings → Git → Deploy Hooks → copiar URL do hook `frontend` → abrir no browser

**Opção 3 — Redeploy manual**
- Vercel → Deployments → `...` → Redeploy

---

## Comandos úteis na EC2

| Comando | Descrição |
|---------|-----------|
| `sudo systemctl status toyota-experience` | Ver status |
| `sudo systemctl restart toyota-experience` | Reiniciar |
| `sudo systemctl stop toyota-experience` | Parar |
| `sudo journalctl -u toyota-experience -f` | Logs em tempo real |
| `sudo journalctl -u toyota-experience -n 100 --no-pager` | Últimas 100 linhas |
| `sudo ss -tlnp \| grep 80` | Verificar porta 80 |

---

## Segurança — Próximos passos

- [ ] Associar Elastic IP à EC2 para IP fixo
- [ ] Restringir Security Group do RDS para aceitar apenas IP da EC2
- [ ] Configurar HTTPS no backend com certificado SSL
- [ ] Adicionar `NEXT_PUBLIC_API_URL` no frontend apontando para o backend
- [ ] Trocar JWT_SECRET por chave gerada aleatoriamente
