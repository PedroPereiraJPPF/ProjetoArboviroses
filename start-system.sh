#!/bin/bash

# Script para iniciar todo o sistema Arboviroses
echo "Iniciando sistema Arboviroses..."

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado. Por favor, instale o Docker primeiro."
    exit 1
fi

if ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose não está disponível. Por favor, verifique a instalação do Docker."
    exit 1
fi

# Copiar arquivo de ambiente
cp .env.docker .env

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker compose -f docker-compose.full.yml down

# Construir e iniciar todos os serviços
echo "🏗️  Construindo e iniciando serviços..."
docker compose -f docker-compose.full.yml up -d --build

# Aguardar um pouco para os serviços subirem
echo "⏳ Aguardando serviços iniciarem..."
sleep 30

# Verificar status dos serviços
echo "📊 Status dos serviços:"
docker compose -f docker-compose.full.yml ps

echo ""
echo "✅ Sistema Arboviroses iniciado com sucesso!"
echo ""
echo "🌐 Acesse as aplicações em:"
echo "   Frontend: http://localhost:3000"
echo "   Backend:  http://localhost:8080"
echo "   Python API: http://localhost:8000"
echo "   Database: localhost:5432"
echo ""
echo "📝 Para ver os logs: docker compose -f docker-compose.full.yml logs -f"
echo "🛑 Para parar: docker compose -f docker-compose.full.yml down"
