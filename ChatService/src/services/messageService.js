const Message = require('../models/Message');
const { getClient } = require('./clientsService');

const saveMessage = async (data) => {
    const newMsg = new Message(data);
    await newMsg.save();
};

const sendMessage = (fromWs, data) => {
    const target = getClient(data.to);

    if (target) {
        target.send(JSON.stringify({
            from: fromWs.key,
            msg: data.msg,
        }));
    } else {
        fromWs.send(JSON.stringify({
            system: `Usuário ${data.to} não está online`,
        }));
    }
};

module.exports = { saveMessage, sendMessage };
