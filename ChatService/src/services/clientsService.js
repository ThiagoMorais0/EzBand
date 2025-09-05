// clientsService.js
const clients = new Map(); // key -> { ws, currentChat }

const addClient = (key, ws) => {
    ws.key = key;
    clients.set(key, ws);
};

const removeClient = (key) => {
    clients.delete(key);
};


module.exports = {
    addClient,
    removeClient
};
