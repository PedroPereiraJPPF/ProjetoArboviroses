#!/bin/bash

# Script para configurar o domínio local arbomonitor
echo "Configurando domínio local arbomonitor..."

# Verificar se já existe a entrada no /etc/hosts
if grep -q "arbomonitor" /etc/hosts; then
    echo "⚠️  Entrada 'arbomonitor' já existe no /etc/hosts"
    echo "Verificando configuração atual:"
    grep "arbomonitor" /etc/hosts
else
    echo "➕ Adicionando entrada para arbomonitor no /etc/hosts..."
    echo "127.0.0.1    arbomonitor" | sudo tee -a /etc/hosts
    echo "✅ Entrada adicionada com sucesso!"
fi

echo ""
echo "🔧 Para testar a configuração:"
echo "   ping arbomonitor"
echo ""
echo "🌐 Após iniciar o sistema, acesse:"
echo "   http://arbomonitor"
echo ""
echo "📝 Para remover a entrada (se necessário):"
echo "   sudo sed -i '/arbomonitor/d' /etc/hosts"
