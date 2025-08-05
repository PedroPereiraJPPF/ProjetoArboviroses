#!/bin/bash

# Script para iniciar todo o sistema Arboviroses com domínio personalizado
echo "🚀 Iniciando sistema Arboviroses com domínio arbomonitor..."

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado. Por favor, instale o Docker primeiro."
    exit 1
fi

if ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose não está disponível. Por favor, verifique a instalação do Docker."
    exit 1
fi

# Configurar o host local se necessário
echo "🔧 Configurando domínio local..."
if ! grep -q "arbomonitor" /etc/hosts; then
    echo "➕ Configurando entrada no /etc/hosts..."
    echo "127.0.0.1    arbomonitor" | sudo tee -a /etc/hosts
    echo "✅ Domínio arbomonitor configurado!"
else
    echo "✅ Domínio arbomonitor já configurado"
fi

# Copiar arquivo de ambiente
if [ -f ".env.docker" ]; then
    cp .env.docker .env
    echo "✅ Arquivo de ambiente configurado"
fi

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker compose -f docker-compose.proxy.yml down

# Construir e iniciar todos os serviços
echo "🏗️  Construindo e iniciando serviços..."
docker compose -f docker-compose.proxy.yml up -d --build

# Aguardar um pouco para os serviços subirem
echo "⏳ Aguardando serviços iniciarem..."
sleep 45

# Verificar status dos serviços
echo "📊 Status dos serviços:"
docker compose -f docker-compose.proxy.yml ps

echo ""
echo "✅ Sistema Arboviroses iniciado com sucesso!"
echo ""
echo "🌐 Acesse as aplicações em:"
echo "   🎯 Frontend: http://arbomonitor"
echo "   📡 Backend API: http://arbomonitor/api"
echo "   🐍 Python API: http://arbomonitor/python-api"
echo "   🔍 Health Check: http://arbomonitor/health"
echo ""
echo "📝 Comandos úteis:"
echo "   📋 Ver logs: docker compose -f docker-compose.proxy.yml logs -f"
echo "   🛑 Parar: docker compose -f docker-compose.proxy.yml down"
echo "   🔄 Reiniciar: docker compose -f docker-compose.proxy.yml restart"
echo ""
echo "🔧 Para testar conectividade:"
echo "   ping arbomonitor"
echo "   curl http://arbomonitor/health"
