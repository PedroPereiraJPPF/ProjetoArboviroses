# 🚀 Guia para Manter o Sistema Arboviroses Rodando Automaticamente

Este guia mostra como configurar o sistema Arboviroses para continuar rodando mesmo após reinicializações da máquina.

## 📋 Pré-requisitos

- Docker e Docker Compose instalados
- Sistema Linux (Ubuntu/Debian recomendado)
- Acesso sudo para configuração de serviços

## 🐳 Opção 1: Docker Compose (Recomendado)

### Instalação do Docker (se não estiver instalado)

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl enable docker
sudo usermod -aG docker $USER
# Fazer logout e login novamente
```

### Configuração e Execução

1. **Configure as variáveis de ambiente:**
   ```bash
   cd /home/pedro/projetos/Arboviroses/ProjetoArboviroses
   cp .env.docker .env
   # Edite o arquivo .env com suas configurações
   ```

2. **Inicie o sistema:**
   ```bash
   ./start-system.sh
   ```

3. **Para parar o sistema:**
   ```bash
   ./stop-system.sh
   ```

### Acessos
- **Frontend:** http://localhost:3000
- **Backend:** http://localhost:8080
- **Python API:** http://localhost:8000
- **Database:** localhost:5432

## ⚙️ Opção 2: Serviço Systemd (Boot Automático)

Para que o sistema inicie automaticamente no boot:

1. **Configure o serviço:**
   ```bash
   sudo ./setup-service.sh
   ```

2. **Comandos do serviço:**
   ```bash
   # Iniciar
   sudo systemctl start arboviroses
   
   # Parar
   sudo systemctl stop arboviroses
   
   # Status
   sudo systemctl status arboviroses
   
   # Ver logs
   sudo journalctl -u arboviroses -f
   
   # Reiniciar
   sudo systemctl restart arboviroses
   ```

## 📊 Monitoramento

### Verificar status dos containers:
```bash
docker-compose -f docker-compose.full.yml ps
```

### Ver logs:
```bash
# Todos os serviços
docker-compose -f docker-compose.full.yml logs -f

# Serviço específico
docker-compose -f docker-compose.full.yml logs -f backend
docker-compose -f docker-compose.full.yml logs -f frontend
docker-compose -f docker-compose.full.yml logs -f python-api
```

### Recursos do sistema:
```bash
docker stats
```

## 🔧 Manutenção

### Atualizar o sistema:
```bash
# Parar serviços
docker-compose -f docker-compose.full.yml down

# Atualizar código
git pull

# Reconstruir e iniciar
docker-compose -f docker-compose.full.yml up -d --build
```

### Backup do banco de dados:
```bash
docker-compose -f docker-compose.full.yml exec db pg_dump -U postgres arboviroses > backup_$(date +%Y%m%d_%H%M%S).sql
```

### Restaurar backup:
```bash
cat backup_file.sql | docker-compose -f docker-compose.full.yml exec -T db psql -U postgres arboviroses
```

## 🚨 Troubleshooting

### Problema: Portas em uso
```bash
# Verificar portas em uso
sudo netstat -tulpn | grep -E ':(3000|8080|8000|5432)'

# Parar processos usando as portas
sudo lsof -ti:3000 | xargs kill -9
sudo lsof -ti:8080 | xargs kill -9
sudo lsof -ti:8000 | xargs kill -9
```

### Problema: Falta de espaço em disco
```bash
# Limpar containers e imagens não utilizadas
docker system prune -a

# Limpar volumes não utilizados (CUIDADO: apaga dados)
docker volume prune
```

### Problema: Erro de permissão
```bash
# Verificar se o usuário está no grupo docker
groups $USER

# Se não estiver, adicionar:
sudo usermod -aG docker $USER
# Fazer logout e login novamente
```

## 🔒 Segurança

### Configurações recomendadas:
1. Altere as senhas padrão no arquivo `.env`
2. Configure firewall para permitir apenas as portas necessárias
3. Use HTTPS em produção (configure certificados SSL)
4. Mantenha o Docker e sistema operacional atualizados

### Firewall (UFW):
```bash
sudo ufw enable
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS
sudo ufw allow 3000  # Frontend
sudo ufw allow 8080  # Backend
sudo ufw allow 8000  # Python API
```

## 📈 Monitoramento Avançado (Opcional)

Para monitoramento mais avançado, você pode adicionar:
- Prometheus + Grafana para métricas
- ELK Stack para logs centralizados
- Uptime monitoring com alertas

## 💡 Dicas

1. **Logs persistentes:** Os logs do Docker são mantidos mesmo após restart
2. **Dados persistentes:** O banco de dados usa volumes Docker para persistência
3. **Auto-restart:** Todos os containers têm `restart: unless-stopped`
4. **Health checks:** Configure health checks para monitoramento automático

## 📞 Suporte

Em caso de problemas:
1. Verifique os logs dos serviços
2. Confirme se todas as portas estão disponíveis
3. Verifique se o Docker está rodando
4. Confirme as configurações do arquivo `.env`
