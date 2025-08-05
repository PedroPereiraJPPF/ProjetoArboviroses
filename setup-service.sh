#!/bin/bash

# Script para configurar o sistema como serviço
echo "⚙️  Configurando Arboviroses como serviço do sistema..."

# Verificar se é executado como root
if [ "$EUID" -ne 0 ]; then
    echo "❌ Este script precisa ser executado como root (sudo)"
    exit 1
fi

# Copiar arquivo de serviço
cp arboviroses.service /etc/systemd/system/

# Recarregar systemd
systemctl daemon-reload

# Habilitar o serviço para iniciar no boot
systemctl enable arboviroses.service

echo "✅ Serviço configurado com sucesso!"
echo ""
echo "🎯 Comandos úteis:"
echo "   Iniciar:  sudo systemctl start arboviroses"
echo "   Parar:    sudo systemctl stop arboviroses"
echo "   Status:   sudo systemctl status arboviroses"
echo "   Logs:     sudo journalctl -u arboviroses -f"
echo ""
echo "🔄 O sistema agora iniciará automaticamente no boot!"
