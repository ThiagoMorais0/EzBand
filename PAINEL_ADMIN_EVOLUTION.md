# 🔐 Painel Admin Evolution API

## 📋 Visão Geral

Painel web elegante e seguro para gerenciamento da instância WhatsApp Evolution API, exclusivo para administradores.

---

## 🌐 Acesso ao Painel

### URL
```
http://localhost:8080/admin/evolution
```

### Credenciais
- **Senha**: Configurada na variável `EVOLUTION_ADMIN_PASSWORD` do arquivo `.env`
- **Padrão**: `admin123456` (⚠️ **ALTERE EM PRODUÇÃO**)

---

## ⚙️ Configuração

### 1. Adicionar ao `.env`
```properties
EVOLUTION_ADMIN_PASSWORD=sua-senha-super-segura-aqui
```

### 2. Adicionar ao `application.properties`
```properties
evolution.admin.password=${EVOLUTION_ADMIN_PASSWORD:admin123}
```

### 3. Adicionar dependência Thymeleaf ao `pom.xml`
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

---

## 🎨 Funcionalidades

### ✅ Autenticação por Senha
- Login seguro com senha de administrador
- Sessão persistente (não precisa relogar a cada acesso)
- Logout manual disponível

### ✅ Gerenciamento de Instância
1. **Criar Instância** - Cria nova instância WhatsApp no Evolution API
2. **Obter QR Code** - Gera QR Code para conectar WhatsApp
3. **Verificar Status** - Mostra se a instância está conectada ou não

### ✅ Interface Elegante
- Design moderno com gradientes roxos
- Responsivo (funciona em desktop e mobile)
- Feedback visual para todas as ações
- Ícones e badges de status
- Mensagens de erro/sucesso claras

---

## 🚀 Como Usar

### Passo 1: Acessar o Painel
1. Abra o navegador em: `http://localhost:8080/admin/evolution`
2. Digite a senha configurada em `EVOLUTION_ADMIN_PASSWORD`
3. Clique em "Entrar"

### Passo 2: Criar Instância WhatsApp
1. No painel, clique em **"Criar Instância WhatsApp"**
2. Aguarde a confirmação de sucesso

### Passo 3: Conectar WhatsApp
1. Clique em **"Obter QR Code"**
2. Um QR Code será exibido na tela
3. No seu celular:
   - Abra o WhatsApp
   - Vá em **Configurações → Aparelhos Conectados**
   - Toque em **"Conectar um aparelho"**
   - Escaneie o QR Code mostrado no painel

### Passo 4: Verificar Conexão
1. Clique em **"Verificar Status"**
2. Veja o badge de status:
   - 🟢 **Conectado** - Instância pronta para enviar mensagens
   - 🔴 **Desconectado** - Precisa conectar via QR Code

---

## 🔒 Segurança

### Proteção Implementada
- ✅ Autenticação por senha
- ✅ Sessão HTTP com timeout automático
- ✅ Validação em todos os endpoints
- ✅ Senha configurável via variável de ambiente

### Boas Práticas
1. **NUNCA use a senha padrão em produção**
2. Use senha forte (mínimo 12 caracteres, letras, números e símbolos)
3. Considere adicionar HTTPS em produção
4. Limite o acesso por IP se possível (via firewall/nginx)

### Exemplo de Senha Forte
```
EVOLUTION_ADMIN_PASSWORD=Mz9#kL@7pQw2$xY5nR8!
```

---

## 📡 Endpoints da API

Todos os endpoints exigem autenticação via sessão.

### Autenticação
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/admin/evolution/auth/login` | Login com senha |
| POST | `/admin/evolution/auth/logout` | Logout |
| GET | `/admin/evolution/auth/check` | Verificar se está autenticado |

### Gerenciamento
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/admin/evolution` | Página do painel admin |
| POST | `/admin/evolution/instance/create` | Criar instância |
| GET | `/admin/evolution/instance/qrcode` | Obter QR Code |
| GET | `/admin/evolution/instance/status` | Verificar status |

---

## 💻 Exemplo de Uso via API

### Login
```bash
curl -X POST http://localhost:8080/admin/evolution/auth/login \
  -H "Content-Type: application/json" \
  -d '{"password": "sua-senha-aqui"}' \
  -c cookies.txt
```

### Criar Instância (após login)
```bash
curl -X POST http://localhost:8080/admin/evolution/instance/create \
  -b cookies.txt
```

### Obter QR Code (após login)
```bash
curl http://localhost:8080/admin/evolution/instance/qrcode \
  -b cookies.txt
```

---

## 🎨 Preview da Interface

### Tela de Login
- Gradiente roxo elegante
- Campo de senha com ícone de cadeado
- Mensagens de erro em caso de senha incorreta
- Design responsivo

### Painel Principal
- Instruções claras de uso
- Badges de status coloridos (verde/vermelho)
- Botões com hover effects
- QR Code exibido em container estilizado
- Loading spinners durante operações

---

## 🐛 Troubleshooting

### Problema: "Senha incorreta"
**Solução**: Verifique se a senha no `.env` está correta e se a aplicação foi reiniciada após alteração.

### Problema: "Erro ao criar instância"
**Solução**: 
1. Verifique se o Evolution API está rodando: `docker ps | grep evolution-api`
2. Verifique os logs: `docker logs evolution-api`

### Problema: QR Code não aparece
**Solução**:
1. Certifique-se de ter criado a instância primeiro
2. Se a instância já está conectada, o QR Code não aparece
3. Verifique o status da instância

### Problema: Página não carrega
**Solução**:
1. Verifique se o Thymeleaf está no `pom.xml`
2. Compile o projeto: `mvn clean install`
3. Reinicie a aplicação

---

## 🔄 Fluxo Completo

```
┌─────────────────────────────────────────┐
│  1. Acessa http://localhost:8080/       │
│     admin/evolution                     │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  2. Digita senha de administrador       │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  3. Clica em "Criar Instância"          │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  4. Clica em "Obter QR Code"            │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  5. Escaneia QR Code no celular         │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  6. Clica em "Verificar Status"         │
│     → 🟢 Conectado!                     │
└─────────────────────────────────────────┘
```

---

## 📚 Arquivos Criados

- **Controller**: `EvolutionAdminController.java`
- **Template HTML**: `templates/evolution-admin.html`
- **Configuração**: Variável `evolution.admin.password`

---

## ⚡ Próximas Melhorias Sugeridas

1. **Logs de Acesso**: Registrar quem acessou e quando
2. **2FA**: Autenticação de dois fatores
3. **Múltiplos Usuários**: Sistema de usuários admin
4. **Histórico**: Mostrar histórico de conexões/desconexões
5. **Webhooks**: Configurar webhooks via painel
6. **Notificações**: Alertas quando instância desconectar

---

**Desenvolvido para EzBand Manager** 🎵  
**Data**: Fevereiro 2026
