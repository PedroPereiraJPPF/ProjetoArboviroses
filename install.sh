#!/bin/bash

# Script de instalação completa do sistema Arboviroses
set -e

echo "🚀 Instalação do Sistema Arboviroses"
echo "===================================="

# Verificar se é Ubuntu/Debian
if ! command -v apt &> /dev/null; then
    echo "❌ Este script é para sistemas Ubuntu/Debian"
    exit 1
fi

# Atualizar sistema
echo "📦 Atualizando sistema..."
sudo apt update && sudo apt upgrade -y

# Instalar Docker
if ! command -v docker &> /dev/null; then
    echo "🐳 Instalando Docker..."
    sudo apt install -y apt-transport-https ca-certificates curl gnupg lsb-release
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt update
    sudo apt install -y docker-ce docker-ce-cli containerd.io
    sudo systemctl enable docker
    sudo usermod -aG docker $USER
    echo "✅ Docker instalado com sucesso"
else
    echo "✅ Docker já está instalado"
fi

# Instalar Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "🔧 Instalando Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo "✅ Docker Compose instalado com sucesso"
else
    echo "✅ Docker Compose já está instalado"
fi

# Configurar firewall básico
echo "🔒 Configurando firewall..."
sudo ufw --force enable
sudo ufw allow 22
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 3000
sudo ufw allow 8080
sudo ufw allow 8000
echo "✅ Firewall configurado"

# Configurar variáveis de ambiente
echo "⚙️  Configurando variáveis de ambiente..."
if [ ! -f .env ]; then
    cp .env.docker .env
    echo "📝 Arquivo .env criado. IMPORTANTE: Edite as senhas!"
fi

# Tornar scripts executáveis
chmod +x start-system.sh
chmod +x stop-system.sh
chmod +x setup-service.sh

echo ""
echo "✅ Instalação concluída com sucesso!"
echo ""
echo "🎯 Próximos passos:"
echo "1. Fazer logout e login para aplicar as permissões do Docker"
echo "2. Editar o arquivo .env com suas configurações"
echo "3. Executar: ./start-system.sh"
echo "4. (Opcional) Configurar como serviço: sudo ./setup-service.sh"
echo ""
echo "📚 Consulte o arquivo DEPLOYMENT_GUIDE.md para instruções detalhadas"
echo ""
echo "⚠️  IMPORTANTE: Faça logout e login antes de continuar!"
