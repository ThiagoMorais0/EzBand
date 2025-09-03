    # 📡 Documentação do WebSocket - Chat 1x1

Este serviço implementa um **servidor WebSocket** para comunicação em tempo real **1x1** (usuário para usuário).  
Todas as mensagens trocadas são persistidas no MongoDB.

---

## 🔑 Autenticação

A autenticação é feita via **token JWT**, enviado na URL de conexão do WebSocket.  
Além do token, é necessário passar também o identificador lógico (`tipoId`) do cliente.

### Exemplo de URL de conexão:
`ws://localhost:3001/?token=SEU_JWT_AQUI&tipoId=user:1` _OBS: quando estiver containerizado, não é localhost_

- `token` → JWT válido, gerado pelo seu back-end.  
- `tipoId` → Identificador único do cliente (ex: `user:1`, `banda:2`).  

Se o token for inválido ou ausente, a conexão será fechada.

---

## 🔌 Conexão

Exemplo em JavaScript:

```
const token = "SEU_JWT_AQUI";
const tipoId = "user:1";

const socket = new WebSocket(`ws://localhost:3001/?token=${token}&tipoId=${tipoId}`); _OBS: quando estiver containerizado, não é localhost_

socket.onopen = () => {
  console.log("Conectado ao servidor!");
};

socket.onclose = () => {
  console.log("Conexão encerrada.");
};

socket.onerror = (err) => {
  console.error("Erro:", err);
};
```

---

## 📤 Envio de mensagens

As mensagens são enviadas em formato JSON com o seguinte esquema:

```
{
  "type": "message",
  "from": "user:1",
  "to": "user:2",
  "msg": "Olá, tudo bem?"
}
```

- `type` → Sempre `"message"` para envio de mensagens.  
- `from` → Identificador do remetente (`tipoId`).  
- `to` → Identificador do destinatário (`tipoId`).  
- `msg` → Texto da mensagem.  

Exemplo em JavaScript:

```
socket.send(JSON.stringify({
  type: "message",
  from: "user:1",
  to: "user:2",
  msg: "Olá, tudo bem?"
}));
```

---

## 📥 Recebimento de mensagens

As mensagens recebidas também chegam em formato JSON.

### Estrutura de mensagem recebida:
```
{
  "from": "user:2",
  "message": "Oi! Estou bem, e você?"
}
```

### Estrutura de mensagem do sistema:
```
{
  "system": "Usuário user:2 não está online"
}
```

Exemplo em JavaScript:

```
socket.onmessage = (event) => {
  const data = JSON.parse(event.data);

  if (data.system) {
    console.log("[SISTEMA]", data.system);
  } else {
    console.log(`${data.from}: ${data.message}`);
  }
};
```

---

## 💾 Persistência

Todas as mensagens trocadas são salvas no MongoDB na coleção `messages`, com a seguinte estrutura:

```
{
  from: String,      // Remetente
  to: String,        // Destinatário
  msg: String,       // Texto da mensagem
  createdAt: Date    // Data/hora do envio
}
```

---

## ✅ Fluxo resumido

1. Cliente conecta via WebSocket com `token` e `tipoId`.  
2. Servidor valida o JWT.  
3. Cliente pode enviar mensagens para outros `tipoId`.  
4. Mensagens são:
   - Salvas no MongoDB.  
   - Encaminhadas em tempo real ao destinatário (se online).  
   - Caso o destinatário esteja offline, remetente recebe notificação do sistema.  
