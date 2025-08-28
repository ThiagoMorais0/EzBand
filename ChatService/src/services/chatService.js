const Message = require('../models/message');

async function saveMessage(msg) {
  const newMsg = new Message(msg);
  return await newMsg.save();
}

async function getMessagesByRoom(room) {
  return await Message.find({ room }).sort({ createdAt: 1 }).lean();
}

module.exports = { saveMessage, getMessagesByRoom };
