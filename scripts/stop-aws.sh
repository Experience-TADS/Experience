#!/bin/bash
# ─────────────────────────────────────────────────────────────
# stop-aws.sh — Para todos os serviços (use no fim da aula)
# Uso: bash scripts/stop-aws.sh
# ─────────────────────────────────────────────────────────────

echo "🛑 Parando todos os serviços..."
docker compose -f docker-compose.aws.yml down

echo "✅ Serviços parados. Lembre-se de parar a EC2 no console AWS!"
echo "   EC2 → Instances → Selecionar → Instance State → Stop"
