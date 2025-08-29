# 📄 Documentação do Serviço WebSocket – Chat 1x1

## 🔹 Visão Geral

Este serviço permite comunicação **1x1 entre usuários** via WebSocket. Ele utiliza **Node.js**, **Socket.IO** e **MongoDB** para persistência das mensagens.

* Cada usuário possui um **id** e um **tipo** (ex: `user`).
* As mensagens são salvas no MongoDB com informações do remetente (`sender`), destinatário (`receiver`) e o texto (`text`).
* Conexões simultâneas do mesmo usuário derrubam sessões antigas.

---

## 🛠 Estrutura de Código

### socketService.js

| Função                       | Descrição                                                                                                                                                                                                           |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `registerUser(socket, user)` | Registra um usuário conectado e armazena seu socket em memória (`connectedUsers`). Se já existir uma conexão do mesmo usuário, a sessão antiga é desconectada. Remove o usuário do `connectedUsers` ao desconectar. |
| `sendMessage(message)`       | Salva a mensagem no MongoDB e envia para o destinatário conectado, se existir.                                                                                                                                      |

**Exemplo de uso:**

```js
registerUser(socket, { id: '1', type: 'user' });

sendMessage({
  sender: { id: '1', type: 'user' },
  receiver: { id: '2', type: 'user' },
  text: 'Olá!'
});
```

### Message.js

Schema do MongoDB:

```js
const userRefSchema = new mongoose.Schema({
  id: { type: String, required: true },
  type: { type: String, required: true },
}, { _id: false });

const messageSchema = new mongoose.Schema({
  sender: { type: userRefSchema, required: true },
  receiver: { type: userRefSchema, required: true },
  text: { type: String, required: true },
  createdAt: { type: Date, default: Date.now }
});
```

* `sender` e `receiver` são **subdocumentos** com `id` e `type`.
* `text` é a mensagem enviada.
* `createdAt` é preenchido automaticamente com a data/hora da criação.

---

## 🌐 Eventos do WebSocket

| Evento           | Payload                                                                                                  | Descrição                            | Resposta                             |
| ---------------- | -------------------------------------------------------------------------------------------------------- | ------------------------------------ | ------------------------------------ |
| `register`       | `{ "id": "1", "type": "user" }`                                                                          | Registra o usuário conectado.        | —                                    |
| `sendMessage`    | `{ "sender": { "id": "1", "type": "user" }, "receiver": { "id": "2", "type": "user" }, "text": "Olá!" }` | Envia uma mensagem e salva no banco. | `receiveMessage` para o destinatário |
| `receiveMessage` | `{ "sender": { "id": "1", "type": "user" }, "receiver": { "id": "2", "type": "user" }, "text": "Olá!" }` | Mensagem recebida pelo destinatário. | —                                    |

**Exemplo de envio:**

```js
socket.emit('sendMessage', {
  sender: { id: '1', type: 'user' },
  receiver: { id: '2', type: 'user' },
  text: 'Olá!'
});
```

**Exemplo de recepção:**

```js
socket.on('receiveMessage', (msg) => {
  console.log(`Mensagem de ${msg.sender.type}:${msg.sender.id} -> ${msg.text}`);
});
```

---

## 💡 Observações importantes

1. O serviço mantém **todos os usuários conectados em memória** (Map) para enviar mensagens em tempo real.
2. Se o destinatário não estiver online, a mensagem é salva, mas **não será emitida** até que ele se conecte.
3. MongoDB deve estar configurado corretamente com usuário e senha:

```js
const uri = `mongodb://root:example@mongo:27017/chat?authSource=admin`;
```

4. Para múltiplos containers, use o **nome do serviço MongoDB** (`mongo`) em vez do IP.
5. Campos obrigatórios: `sender.id`, `sender.type`, `receiver.id`, `receiver.type`, `text`.

---

## ⚡ Sugestão

Você pode gerar documentação interativa utilizando **AsyncAPI** se quiser compartilhar com outros desenvolvedores ou equipes.
