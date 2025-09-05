const { WebSocketServer } = require("ws");
const { verifyToken } = require('../config/jwt');
const connectDB = require('../config/db');
const { addClient, removeClient } = require('../services/clientsService');
const { saveMessage, sendMessage, getUserChats, getChatMessages, readChat } = require('../services/messageService');

const wss = new WebSocketServer({ port: 3001 });

wss.on("connection", async (ws, req) => {
    try {
        await connectDB();

        const url = new URL(req.url, `http://${req.headers.host}`);
        const token = url.searchParams.get("token");
        const tipoIdConectado = url.searchParams.get("tipoId");

        if (!token) {
            ws.close(4001, "Token ausente");
            return;
        }

        verifyToken(token);

        addClient(tipoIdConectado, ws);
        console.log(`Usuário conectado: ${tipoIdConectado}`);

        const chats = await getUserChats(tipoIdConectado);
        ws.send(JSON.stringify({ type: 'chats', data: chats }));

        ws.on("message", async (msg) => {
            try {
                const data = JSON.parse(msg);

                // salva e envia mensagens
                if (data.type === "message") {
                    let msg = data;
                    msg.from = ws.key;
                    await saveMessage(msg);
                    await sendMessage(msg); 
                }

                // Solicitação de mais chats
                if (data.type === "getChats") {
                    const page = data.page || 1;
                    const chats = await getUserChats(tipoIdConectado, page, 10);
                    ws.send(JSON.stringify({ type: "chats", data: chats }));
                    return;
                }

                // Busca as mensagens do chat
                if (data.type === "getChatMessages") {
                    const { userB, page = 1 } = data;
                    const messages = await getChatMessages(ws.key, userB, page);
                    ws.send(JSON.stringify({ type: "chatMessages", data: messages }));
                    await readChat(ws.key, userB);
                    return;
                }

                // marca as mensagens do chat como lidas
                if (data.type === "readChat") {
                    const { userB } = data;
                    await readChat(ws.key, userB);
                    return;
                }

            } catch (err) {
                console.error("Erro na mensagem:", err);
            }
        });

        ws.on("close", () => {
            removeClient(tipoIdConectado);
            console.log(`Usuário desconectado: ${tipoIdConectado}`);
        });

    } catch (err) {
        console.log("Falha JWT:", err.message);
        ws.close(4002, "Token inválido");
    }
});

console.log("Servidor WebSocket 1x1 rodando na porta 3001");
