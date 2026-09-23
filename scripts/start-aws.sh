#!/bin/bash
# ─────────────────────────────────────────────────────────────
# start-aws.sh — Inicia todos os serviços na EC2
# Uso: bash scripts/start-aws.sh
# ─────────────────────────────────────────────────────────────

set -e

echo "🚀 Iniciando Experience na AWS..."

# Verifica se o .env existe
if [ ! -f .env ]; then
  echo "❌ Arquivo .env não encontrado!"
  echo "Crie o arquivo .env com as variáveis necessárias:"
  echo "  POSTGRES_HOST=<endpoint-rds>"
  echo "  POSTGRES_DB=db_experience"
  echo "  POSTGRES_USER=postgres"
  echo "  POSTGRES_PASSWORD=<senha>"
  echo "  JWT_SECRET=<chave-base64>"
  exit 1
fi

# Puxar atualizações do repositório
echo "📥 Atualizando código..."
git pull origin main

# Build e inicialização dos containers
echo "🐳 Subindo containers..."
docker compose -f docker-compose.aws.yml up --build -d

# Aguarda a API ficar pronta
echo "⏳ Aguardando API inicializar..."
sleep 20

# Mostra status
docker compose -f docker-compose.aws.yml ps

echo ""
echo "✅ Todos os serviços estão no ar!"
echo ""
echo "📌 Acesse pelo IP público da EC2:"
echo "   API:       http://<IP>:8080/swagger-ui.html"
echo "   Chatbot:   http://<IP>:5000"
echo "   Node-RED:  http://<IP>:1880"
echo "   InfluxDB:  http://<IP>:8086"
echo "   Grafana:   http://<IP>:3001"
