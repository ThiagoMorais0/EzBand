const Message = require('../models/Message');

// Map chave: "tipo:id" -> socket
const connectedUsers = new Map();

function registerUser(socket, user) {
    const key = `${user.type}:${user.id}`;

    // Se já existe conexão desse usuário, derruba a antiga
    const existing = connectedUsers.get(key);
    if (existing && existing.id !== socket.id) {
        console.log(`Desconectando sessão antiga de ${key}`);
        existing.disconnect(true);
    }

    connectedUsers.set(key, socket);

    socket.on('disconnect', () => {
        if (connectedUsers.get(key) === socket) {
            connectedUsers.delete(key);
            console.log(`Usuário desconectado: ${key}`);
        }
    });
}

function sendMessage(message) {
    // 1. salvar no MongoDB
    const newMsg = new Message(message);
    newMsg.save()
        .then(() => console.log("Mensagem salva no MongoDB"))
        .catch(err => console.error("Erro ao salvar mensagem:", err));

    const key = `${message.receiver.type}:${message.receiver.id}`;
    const socket = connectedUsers.get(key);

    if (socket) {
        socket.emit('receiveMessage', message);
    } else {
        console.log(`Destinatário ${key} não está online`);
    }
}

module.exports = { registerUser, sendMessage };
