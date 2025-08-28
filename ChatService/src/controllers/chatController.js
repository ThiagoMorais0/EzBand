const { saveMessage, getMessagesByRoom } = require('../services/chatService');
const { getConversationRoom } = require('../utils/roomUtils');

const chatHandler = (io, socket) => {
  console.log('Novo usuário conectado:', socket.id);

  socket.on('registerUser', ({ id, type }) => {
    const roomName = `${type}_${id}`;
    socket.join(roomName); // sala exclusiva do usuário
    console.log(`Usuário registrado na sala: ${roomName}`);
  });

  socket.on('sendMessage', async (msg) => {
    // Gera a sala única da conversa
    const room = getConversationRoom(msg.sender, msg.receiver);

    // Salva a mensagem com a sala correta
    const saved = await saveMessage({ ...msg, room });

    // Envia para todos que estão nessa sala
    io.to(room).emit('receiveMessage', saved);
  });

  socket.on('joinRoom', async (users) => {
    if (!Array.isArray(users) || users.length < 2) return;
    const room = getConversationRoom(users[0], users[1]); // ordena e cria room
    socket.join(room);
  
    const messages = await getMessagesByRoom(room);
    socket.emit('roomHistory', messages);
  });

  socket.on('disconnect', () => {
    console.log('Usuário desconectado:', socket.id);
  });
};

module.exports = chatHandler;
