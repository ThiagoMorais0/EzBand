// clientsService.js
const clients = new Map(); // key -> { ws, currentChat }

const addClient = (key, ws) => {
    ws.key = key;
    clients.set(key, { ws, currentChat: null });
};

const removeClient = (key) => {
    clients.delete(key);
};

const getClient = (key) => {
    const client = clients.get(key);
    return client ? client.ws : null;
};

// Retorna todo o objeto { ws, currentChat }
const getClientData = (key) => clients.get(key);

// Atualiza o chat atualmente aberto para o usuário
const setCurrentChat = (key, chatUser) => {
    const client = clients.get(key);
    if (client) client.currentChat = chatUser;
};

// Retorna o chat atualmente aberto para o usuário
const getCurrentChat = (key) => {
    const client = clients.get(key);
    return client ? client.currentChat : null;
};

module.exports = {
    addClient,
    removeClient,
    getClient,
    getClientData,
    setCurrentChat,
    getCurrentChat
};
