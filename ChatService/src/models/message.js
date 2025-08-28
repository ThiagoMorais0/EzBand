// src/models/message.js
const mongoose = require('mongoose');

const messageSchema = new mongoose.Schema({
  sender: {
    id: { type: String, required: true },   // ID no sistema externo (Postgres)
    type: { 
      type: String, 
      enum: ['USUARIO', 'BANDA', 'ESTUDIO', 'LOCAL_EVENTO'], 
      required: true 
    }
  },
  receiver: {
    id: { type: String, required: true },
    type: { 
      type: String, 
      enum: ['USUARIO', 'BANDA', 'ESTUDIO', 'LOCAL_EVENTO'], 
      required: true 
    }
  },
  content: { type: String, required: true },
  room: { type: String, required: true },   // opcional, se quiser agrupar conversas
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Message', messageSchema);
