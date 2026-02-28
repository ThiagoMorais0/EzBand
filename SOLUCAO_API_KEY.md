# 🔑 Solução: Erro 401 Unauthorized - Evolution API

## 🎯 Problema

```
401 UNAUTHORIZED - {"status":401,"error":"Unauthorized","response":{"message":"Unauthorized"}}
```

Isso ocorre quando a **API Key** enviada pelo Spring Boot **não corresponde** à API Key configurada no Evolution API.

---

## ✅ Solução Passo a Passo

### **1. Parar os containers**
```bash
docker-compose down
```

### **2. Definir a mesma API Key no `.env`**

Abra o arquivo `.env` e defina uma API Key **segura**:

```properties
# Use a MESMA chave em ambas as variáveis
EVOLUTION_API_KEY=minha-chave-super-secreta-123456
```

**⚠️ IMPORTANTE:** A chave deve ter pelo menos 10 caracteres e ser segura!

### **3. Verificar o arquivo `.env`**

Certifique-se de que o `.env` contém:

```properties
# Database Evolution
EVO_DB_USER=evolution
EVO_DB_PASSWORD=evolution123

# Evolution API
EVOLUTION_API_KEY=minha-chave-super-secreta-123456
EVOLUTION_API_URL=http://evolution-api:8081
EVOLUTION_SERVER_URL=http://localhost:8081
EVOLUTION_INSTANCE_NAME=ezband-whatsapp

# Admin password
EVOLUTION_ADMIN_PASSWORD=sua-senha-admin-aqui
```

### **4. Verificar `application.properties`**

No arquivo:
```
D:\dev\EzBand\EzBand\EzBand-Backend\src\main\resources\application.properties
```

Certifique-se de que tem:

```properties
# Evolution API
evolution.api.url=${EVOLUTION_API_URL:http://localhost:8081}
evolution.api.key=${EVOLUTION_API_KEY:change-me}
evolution.instance.name=${EVOLUTION_INSTANCE_NAME:ezband-whatsapp}
evolution.admin.password=${EVOLUTION_ADMIN_PASSWORD:admin123}
```

### **5. Recriar os containers**

```bash
# Recriar apenas os serviços necessários
docker-compose up -d redis db_evolution evolution-api
```

### **6. Verificar logs do Evolution API**

```bash
docker logs evolution-api
```

Você deve ver algo como:
```
Evolution API started on port 8080
```

### **7. Reiniciar a aplicação Spring Boot**

```bash
# Parar (Ctrl+C)
# Rodar novamente
mvn spring-boot:run
```

### **8. Verificar os logs do Spring Boot**

Ao iniciar, você verá:
```
=== Evolution API Configuration ===
URL: http://localhost:8081
API Key configurada: ✅ Sim
API Key length: 34
API Key preview: minha-ch...3456
Instance Name: ezband-whatsapp
===================================
```

Se aparecer **"❌ NÃO (usando valor padrão)"**, a variável não está sendo lida!

---

## 🔍 Diagnóstico

### **Teste 1: Verificar se Evolution API está respondendo**

```bash
curl http://localhost:8081
```

Deve retornar informações sobre a API.

### **Teste 2: Testar autenticação manualmente**

```bash
curl -X POST http://localhost:8081/instance/create \
  -H "apikey: minha-chave-super-secreta-123456" \
  -H "Content-Type: application/json" \
  -d '{
    "instanceName": "teste",
    "qrcode": true,
    "integration": "WHATSAPP-BAILEYS"
  }'
```

- ✅ Se funcionar: API Key está correta
- ❌ Se der 401: API Key está errada ou Evolution API não está configurado

### **Teste 3: Verificar variável de ambiente no container**

```bash
docker exec evolution-api env | grep AUTHENTICATION_API_KEY
```

Deve mostrar:
```
AUTHENTICATION_API_KEY=minha-chave-super-secreta-123456
```

---

## 🐛 Problemas Comuns

### Problema 1: "API Key configurada: ❌ NÃO"

**Causa**: Variável de ambiente não está sendo lida

**Solução**:
1. Verifique se o `.env` está no diretório raiz do projeto
2. Reinicie completamente a aplicação
3. Se estiver usando IDE, reinicie a IDE para recarregar o `.env`

### Problema 2: API Key diferente entre Docker e Spring

**Causa**: `.env` tem valores diferentes

**Solução**:
Use **exatamente a mesma** API Key:
```properties
EVOLUTION_API_KEY=valor-exato-aqui
```

### Problema 3: Container não usa `.env`

**Causa**: Docker Compose não encontrou o `.env`

**Solução**:
```bash
# Parar tudo
docker-compose down -v

# Verificar se o .env existe
ls -la .env

# Subir novamente
docker-compose up -d redis db_evolution evolution-api
```

---

## 📋 Checklist Final

Antes de testar novamente, confirme:

- [ ] `.env` existe no diretório raiz
- [ ] `EVOLUTION_API_KEY` está definida no `.env`
- [ ] `application.properties` tem `evolution.api.key=${EVOLUTION_API_KEY:change-me}`
- [ ] Containers foram recriados com `docker-compose up -d`
- [ ] Aplicação Spring Boot foi reiniciada
- [ ] Logs mostram "API Key configurada: ✅ Sim"

---

## 🎯 Teste Final

Após seguir todos os passos:

1. Acesse: `http://localhost:8080/admin/evolution`
2. Faça login com a senha admin
3. Clique em **"Criar Instância WhatsApp"**
4. Deve funcionar! ✅

Se continuar com erro 401, verifique os logs e compare a API Key sendo enviada vs. a configurada no Evolution API.
