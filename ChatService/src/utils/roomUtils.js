// src/utils/roomUtils.js
function getConversationRoom(sender, receiver) {
    const ids = [
      `${sender.type}_${sender.id}`,
      `${receiver.type}_${receiver.id}`
    ].sort(); // ordena alfabeticamente
    return `chat_${ids[0]}_${ids[1]}`;
  }
  
  module.exports = { getConversationRoom };
  