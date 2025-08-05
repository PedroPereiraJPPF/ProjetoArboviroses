# 🌐 Como Acessar o Projeto Arboviroses

## ✅ Status Atual
O sistema está rodando com sucesso em containers Docker!

## 🔗 URLs de Acesso

### Frontend (Interface do Usuário)
- **URL**: http://localhost:3000
- **Descrição**: Interface React/Vite para interação com o sistema
- **Status**: ✅ Funcionando

### Backend (API Spring Boot)
- **URL**: http://localhost:8080
- **Descrição**: API REST principal do sistema
- **Documentação**: http://localhost:8080/swagger-ui.html (se disponível)
- **Status**: ✅ Funcionando

### Python API (Machine Learning)
- **URL**: http://localhost:8000
- **Descrição**: API FastAPI para processamento de ML
- **Documentação**: http://localhost:8000/docs
- **Status**: ✅ Funcionando

### Banco de Dados
- **Host**: localhost
- **Porta**: 5432
- **Database**: arboviroses_db
- **Usuário**: pedro
- **Senha**: 123456
- **Status**: ✅ Funcionando

## 🔧 Comandos Úteis

### Ver Status dos Serviços
```bash
docker compose -f docker-compose.simple.yml ps
```

### Ver Logs dos Serviços
```bash
# Todos os logs
docker compose -f docker-compose.simple.yml logs -f

# Log específico (substitua SERVICE por: frontend, backend, python-api, db)
docker compose -f docker-compose.simple.yml logs -f SERVICE
```

### Parar o Sistema
```bash
docker compose -f docker-compose.simple.yml down
```

### Iniciar o Sistema
```bash
docker compose -f docker-compose.simple.yml up -d
```

### Reiniciar um Serviço Específico
```bash
docker compose -f docker-compose.simple.yml restart SERVICE
```

## 🔄 Inicialização Automática

Para que o sistema inicie automaticamente após reinicialização da máquina:

1. Execute o script de configuração:
```bash
sudo ./setup-service.sh
```

2. O serviço será configurado no systemd com:
```bash
# Verificar status
sudo systemctl status arboviroses

# Iniciar manualmente
sudo systemctl start arboviroses

# Parar manualmente
sudo systemctl stop arboviroses

# Ver logs do serviço
sudo journalctl -u arboviroses -f
```

## 🛠️ Solução de Problemas

### Problema de CORS
✅ **Resolvido**: O backend agora aceita requisições do frontend (porta 3000)

### Frontend não carrega
1. Verifique se o container está rodando:
   ```bash
   docker compose -f docker-compose.simple.yml ps
   ```
2. Verifique os logs:
   ```bash
   docker compose -f docker-compose.simple.yml logs frontend
   ```

### Backend não responde
1. Verifique health check:
   ```bash
   curl http://localhost:8080/actuator/health
   ```
2. Verifique conexão com banco:
   ```bash
   docker compose -f docker-compose.simple.yml logs backend
   ```

### Porta ocupada
Se alguma porta estiver ocupada:
```bash
# Ver processos usando uma porta (substitua PORTA)
lsof -i :PORTA

# Ou parar todos os containers
docker compose -f docker-compose.simple.yml down
```

## 📋 Checklist de Verificação

- [x] Docker e Docker Compose instalados
- [x] Containers buildados com sucesso
- [x] Todos os serviços rodando
- [x] CORS configurado corretamente
- [x] Frontend acessível na porta 3000
- [x] Backend acessível na porta 8080
- [x] Python API acessível na porta 8000
- [x] Banco de dados funcionando

## 🎉 Próximos Passos

1. **Acesse o frontend**: http://localhost:3000
2. **Teste as funcionalidades** da aplicação
3. **Configure o serviço automático** se necessário
4. **Monitore os logs** para identificar possíveis problemas

O sistema está **100% funcional** e pronto para uso! 🚀
