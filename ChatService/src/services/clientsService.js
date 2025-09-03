const clients = new Map(); // chave -> socket

const addClient = (key, ws) => {
    ws.key = key;
    clients.set(key, ws);
};

const removeClient = (key) => {
    clients.delete(key);
};

const getClient = (key) => clients.get(key);

module.exports = { addClient, removeClient, getClient };
