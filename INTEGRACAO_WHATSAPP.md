# 📱 Integração WhatsApp - EzBand Manager

## 📋 Visão Geral

Este documento descreve a integração de notificações via WhatsApp no EzBand Manager utilizando a **Evolution API v2.1.1** como engine de envio. A arquitetura foi projetada para permitir a substituição futura pela Cloud API da Meta (WhatsApp Business API oficial) sem alteração na lógica de negócio.

---

## 🏗️ Arquitetura

### Design Pattern: Service Abstraction

```
┌─────────────────────────────────────────────────────────────┐
│                     Business Logic Layer                     │
│                   (Controllers, Services)                    │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
                  ┌──────────────────────┐
                  │  WhatsappService     │  ◄── Interface abstrata
                  │    (Interface)       │
                  └──────────────────────┘
                             │
                ┌────────────┴────────────┐
                ▼                         ▼
    ┌─────────────────────┐   ┌─────────────────────┐
    │ EvolutionApiService │   │  CloudApiService    │
    │     Impl (Atual)    │   │   Impl (Futuro)     │
    └─────────────────────┘   └─────────────────────┘
                │                         │
                ▼                         ▼
         Evolution API              Meta Cloud API
```

**Benefício**: Trocar de provider é apenas criar uma nova implementação e alterar o bean injetado.

---

## 🚀 Configuração Inicial

### 1. Substituir compose.yml

O arquivo `compose.yml.new` foi criado com os serviços necessários. Substitua o arquivo atual:

```bash
cd D:\dev\EzBand\EzBand
mv compose.yml.old compose.yml.old.backup
mv compose.yml.old.new compose.yml.old
```

### 2. Configurar Variáveis de Ambiente

Copie o arquivo `.env.example` para `.env` e configure:

```bash
cp .env.example .env
```

Edite o arquivo `.env` e defina valores seguros:

```properties
# Evolution API Configuration
EVOLUTION_API_KEY=sua-chave-super-secreta-aqui-123456
EVOLUTION_API_URL=http://evolution-api:8081
EVOLUTION_SERVER_URL=http://localhost:8081
EVOLUTION_INSTANCE_NAME=ezband-whatsapp
```

⚠️ **IMPORTANTE**: Altere `EVOLUTION_API_KEY` para um valor seguro e único!

### 3. Adicionar Propriedades na Aplicação

Copie as configurações de `application.properties.example` para seu `application.properties`:

```properties
# Evolution API
evolution.api.url=${EVOLUTION_API_URL:http://localhost:8081}
evolution.api.key=${EVOLUTION_API_KEY:change-me}
evolution.instance.name=${EVOLUTION_INSTANCE_NAME:ezband-whatsapp}

# Retry
spring.retry.enabled=true
```

### 4. Iniciar Serviços

```bash
docker-compose up -d redis evolution-api
```

Aguarde 30 segundos para os serviços iniciarem completamente.

### 5. Compilar e Iniciar Aplicação

```bash
cd EzBand-Backend
mvn clean install
mvn spring-boot:run
```

---

## 📡 Fluxo de Uso

### Passo 1: Criar Instância WhatsApp

```bash
curl -X POST http://localhost:8080/api/v1/whatsapp/instancia/criar
```

**Resposta esperada:**
```json
{
  "instance": {
    "instanceName": "ezband-whatsapp",
    "status": "created"
  }
}
```

### Passo 2: Obter QR Code

```bash
curl http://localhost:8080/api/v1/whatsapp/instancia/qrcode
```

**Resposta esperada:**
```json
{
  "qrcode": {
    "code": "data:image/png;base64,iVBORw0KGgoAAAANSU...",
    "base64": "iVBORw0KGgoAAAANSU..."
  }
}
```

### Passo 3: Escanear QR Code

1. Abra o WhatsApp no seu celular
2. Vá em **Configurações → Aparelhos conectados → Conectar um aparelho**
3. Escaneie o QR Code retornado pela API
4. Aguarde a confirmação de conexão

### Passo 4: Verificar Status

```bash
curl http://localhost:8080/api/v1/whatsapp/instancia/status
```

**Resposta esperada quando conectado:**
```json
{
  "instance": {
    "instanceName": "ezband-whatsapp",
    "state": "open"
  }
}
```

### Passo 5: Enviar Mensagem de Teste

```bash
curl -X POST http://localhost:8080/api/v1/whatsapp/teste \
  -H "Content-Type: application/json" \
  -d '{
    "numero": "5511999999999",
    "mensagem": "🎵 Olá do EzBand Manager!"
  }'
```

**Resposta esperada:**
```json
{
  "sucesso": true,
  "mensagem": "Mensagem de teste enviada com sucesso (assíncrona)",
  "numero": "5511999999999",
  "conteudo": "🎵 Olá do EzBand Manager!",
  "info": "Verifique os logs para confirmar o envio"
}
```

---

## 🔧 API Endpoints

### 1. Gerenciamento de Instância

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST   | `/api/v1/whatsapp/instancia/criar` | Cria instância WhatsApp |
| GET    | `/api/v1/whatsapp/instancia/status` | Verifica status de conexão |
| GET    | `/api/v1/whatsapp/instancia/qrcode` | Obtém QR Code para conectar |

### 2. Envio de Mensagens

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST   | `/api/v1/whatsapp/mensagem/enviar` | Envia mensagem de texto |
| POST   | `/api/v1/whatsapp/mensagem/enviar-midia` | Envia mídia (imagem, vídeo, doc) |
| POST   | `/api/v1/whatsapp/teste` | Endpoint de teste |

### 3. Exemplo de Envio de Mensagem

```bash
curl -X POST http://localhost:8080/api/v1/whatsapp/mensagem/enviar \
  -H "Content-Type: application/json" \
  -d '{
    "numero": "5511999999999",
    "mensagem": "Olá! Seu ensaio está agendado para amanhã às 19h."
  }'
```

### 4. Exemplo de Envio de Mídia

```bash
curl -X POST http://localhost:8080/api/v1/whatsapp/mensagem/enviar-midia \
  -H "Content-Type: application/json" \
  -d '{
    "numero": "5511999999999",
    "mediaUrl": "https://exemplo.com/imagem.jpg",
    "mediaType": "image",
    "caption": "Logo da banda atualizada!"
  }'
```

---

## 📊 Auditoria e Rastreabilidade

Todas as mensagens enviadas são registradas na tabela `whatsapp_message_log` com:

- ✅ Número de destino (formato E.164)
- ✅ Conteúdo da mensagem
- ✅ Provider utilizado (EVOLUTION_API)
- ✅ Message ID retornado pela API
- ✅ Status do envio (ENVIADO, FALHA, etc)
- ✅ Data e hora do envio
- ✅ Mensagem de erro (se houver)

### Consultar Logs

```sql
-- Últimas 10 mensagens enviadas
SELECT * FROM whatsapp_message_log 
ORDER BY data_envio DESC 
LIMIT 10;

-- Mensagens com falha
SELECT * FROM whatsapp_message_log 
WHERE status = 'FALHA';

-- Mensagens por destinatário
SELECT * FROM whatsapp_message_log 
WHERE destino = '+5511999999999';
```

---

## 🔄 Características Implementadas

### ✅ Resiliência
- **Retry Automático**: 3 tentativas com backoff exponencial (2s, 4s, 8s)
- **Async**: Envios não bloqueiam a thread principal
- **Logs**: Todas as falhas são registradas com detalhes

### ✅ Normalização de Números
- Converte para formato E.164 automaticamente
- Validação com biblioteca `libphonenumber`
- Exemplo: `11999999999` → `+5511999999999`

### ✅ Future-Proof
- Interface abstrata permite trocar provider
- DTOs genéricos para ambas APIs
- Logs incluem campo `provider` para auditoria

---

## 🔮 Preparação para Cloud API (Futuro)

Quando migrar para a Cloud API da Meta:

1. **Criar nova implementação:**
   ```java
   @Service
   @Primary // ← Marca como implementação padrão
   public class MetaCloudApiServiceImpl implements WhatsappService {
       // Implementação usando Cloud API
   }
   ```

2. **Atualizar configuração:**
   ```properties
   whatsapp.provider=cloud-api
   cloud.api.access.token=SEU_TOKEN_AQUI
   cloud.api.phone.number.id=ID_DO_NUMERO
   ```

3. **Pronto!** A lógica de negócio permanece inalterada.

---

## 🛠️ Troubleshooting

### Problema: "Instância não encontrada"

**Solução:** Crie a instância primeiro:
```bash
curl -X POST http://localhost:8080/api/v1/whatsapp/instancia/criar
```

### Problema: "QR Code expirado"

**Solução:** Gere um novo QR Code:
```bash
curl http://localhost:8080/api/v1/whatsapp/instancia/qrcode
```

### Problema: Mensagem não é enviada

**Verificações:**
1. Instância está conectada? `GET /api/v1/whatsapp/instancia/status`
2. Número está no formato correto? (DDI + DDD + Número)
3. Evolution API está rodando? `docker ps | grep evolution-api`
4. Verifique os logs: `docker logs evolution-api`

### Problema: Evolution API não inicia

**Solução:**
```bash
# Verificar logs
docker logs evolution-api

# Reiniciar serviço
docker-compose restart evolution-api

# Verificar se Redis está rodando
docker ps | grep redis
```

---

## 📝 Dependências Adicionadas

```xml
<!-- Spring Retry (resiliência) -->
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>

<!-- Spring Aspects (necessário para @Retryable) -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
</dependency>

<!-- LibPhoneNumber (normalização de números) -->
<dependency>
    <groupId>com.google.i18n</groupId>
    <artifactId>libphonenumber</artifactId>
    <version>8.13.26</version>
</dependency>
```

---

## 📚 Documentação Adicional

- **Evolution API Docs**: https://doc.evolution-api.com/
- **WhatsApp Cloud API**: https://developers.facebook.com/docs/whatsapp/cloud-api
- **LibPhoneNumber**: https://github.com/google/libphonenumber

---

## ✅ Checklist de Implantação

- [ ] Substituir `compose.yml` pelo novo arquivo
- [ ] Configurar `.env` com chaves seguras
- [ ] Adicionar propriedades no `application.properties`
- [ ] Executar `docker-compose up -d redis evolution-api`
- [ ] Compilar aplicação: `mvn clean install`
- [ ] Criar instância WhatsApp via API
- [ ] Escanear QR Code no celular
- [ ] Enviar mensagem de teste
- [ ] Verificar tabela `whatsapp_message_log` no banco

---

## 🎯 Próximos Passos (Sugestões)

1. **Webhook**: Implementar recebimento de mensagens via webhook
2. **Templates**: Criar templates de mensagens para notificações
3. **Agendamento**: Agendar mensagens para envio futuro
4. **Grupos**: Implementar envio para grupos WhatsApp
5. **Dashboard**: Criar painel de monitoramento de envios

---

**Desenvolvido para EzBand Manager** 🎵  
**Versão Evolution API**: v2.1.1  
**Data**: Fevereiro 2026
