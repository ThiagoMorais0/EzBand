const { WebSocketServer } = require("ws");
const { verifyToken } = require('../config/jwt');
const connectDB = require('../config/db');
const { addClient, removeClient } = require('../services/clientsService');
const { saveMessage, sendMessage } = require('../services/messageService');

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

        ws.on("message", async (msg) => {
            try {
                const data = JSON.parse(msg);
                console.log(data);

                await saveMessage(data);

                if (data.type === "message") {
                    sendMessage(ws, data);
                }
            } catch (err) {
                console.error("Erro na mensagem:", err);
            }
        });

        ws.on("close", () => {
            removeClient(ws.key);
            console.log(`Usuário desconectado: ${ws.key}`);
        });

    } catch (err) {
        console.log("Falha JWT:", err.message);
        ws.close(4002, "Token inválido");
    }
});

console.log("Servidor WebSocket 1x1 rodando na porta 3001");
