#!/bin/bash

# Script para parar o sistema Arboviroses
echo "🛑 Parando sistema Arboviroses..."

# Parar todos os containers do sistema com proxy
docker compose -f docker-compose.proxy.yml down

echo "✅ Sistema parado com sucesso!"
echo ""
echo "📝 Para iniciar novamente:"
echo "   ./start-arbomonitor.sh"
echo ""
echo "🔧 Para remover o domínio local (opcional):"
echo "   sudo sed -i '/arbomonitor/d' /etc/hosts"
