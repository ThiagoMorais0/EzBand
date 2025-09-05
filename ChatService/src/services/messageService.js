const Message = require('../models/Message');

const saveMessage = async (data) => {
    const newMsg = new Message(data);
    await newMsg.save();
};

const sendMessage = async (data) => {
    const targetClient = getClientData(data.to);

    if (targetClient) {
        targetClient.ws.send(JSON.stringify({
            from: data.from,
            msg: data.msg,
            read: isChatOpen
        }));
    } else {
        fromWs.send(JSON.stringify({
            system: `Usuário ${data.to} não está online`,
        }));
    }
};

const getUserChats = async (userId, page = 1, pageSize = 10) => {
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


    return messages;
};

const readChat = async (userA, userB) => {
    await Message.updateMany(
        { from: userB, to: userA, read: false },
        { $set: { read: true } }
    );
}

module.exports = { saveMessage, sendMessage, getUserChats, getChatMessages, readChat };
