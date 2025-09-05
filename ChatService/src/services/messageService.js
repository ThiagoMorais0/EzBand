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

const getUserChats = async (userId, page = 1, pageSize = 10) => {
    console.log("userId: " + userId);

    const skip = (page - 1) * pageSize;

    const chats = await Message.aggregate([
        { $match: { $or: [{ from: userId }, { to: userId }] } },
        { $sort: { createdAt: -1 } },
        {
            $group: {
                _id: {
                    participants: {
                        $cond: [
                            { $lt: ["$from", "$to"] },
                            ["$from", "$to"],
                            ["$to", "$from"]
                        ]
                    }
                },
                lastMessage: { $first: "$$ROOT" },
                updatedAt: { $first: "$createdAt" }
            }
        },
        {
            $project: {
                _id: 0,
                userA: { $arrayElemAt: ["$_id.participants", 0] },
                userB: { $arrayElemAt: ["$_id.participants", 1] },
                lastMessage: {
                    content: "$lastMessage.msg",
                    sender: "$lastMessage.from",
                    createdAt: "$lastMessage.createdAt",
                    read: "$lastMessage.read"
                },
                updatedAt: 1
            }
        },
        { $sort: { updatedAt: -1 } },
        { $skip: skip },
        { $limit: pageSize }
    ]);
    return chats;
};

const getChatMessages = async (userA, userB, page = 1) => {
    const pageSize = 25;
    const skip = (page - 1) * pageSize;

    // Buscar mensagens entre os dois usuários
    const messages = await Message.find({
        $or: [
            { from: userA, to: userB },
            { from: userB, to: userA }
        ]
    })
        .sort({ createdAt: -1 })
        .skip(skip)
        .limit(pageSize)
        .lean();

    // Marcar como lidas as mensagens enviadas para o usuário conectado
    await Message.updateMany(
        { to: userA, from: userB, read: false },
        { $set: { read: true } }
    );

    return messages;
}



module.exports = { saveMessage, sendMessage, getUserChats, getChatMessages };
