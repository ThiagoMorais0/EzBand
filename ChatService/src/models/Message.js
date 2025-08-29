const mongoose = require('mongoose');

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

module.exports = mongoose.model('Message', messageSchema);
