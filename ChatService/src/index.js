require('dotenv').config();
const express = require('express');
const jwt = require('jsonwebtoken'); // <-- usar jsonwebtoken diretamente
const http = require('http');
const { Server } = require('socket.io');
const mongoose = require('mongoose');
const cors = require('cors');
const chatHandler = require('./controllers/chatController');

const app = express();
app.use(cors());
app.use(express.json());

const server = http.createServer(app);

const io = new Server(server, {
    cors: { origin: '*' }
});

io.use((socket, next) => {
    const token = socket.handshake.auth?.token;
    if (!token) return next(new Error('Token não fornecido'));

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        socket.user = { id: decoded.id, type: decoded.tipo };
        next();
    } catch (err) {
        console.log("Token inválido ou expirado");
        next(new Error('Token inválido ou expirado'));
    }
});

io.on('connection', (socket) => chatHandler(io, socket));

mongoose.connect(process.env.MONGO_URI)
    .then(() => console.log('MongoDB conectado'))
    .catch(err => console.log('Erro ao conectar MongoDB:', err));

server.listen(process.env.PORT, () => {
    console.log(`Chat Service rodando na porta ${process.env.PORT}`);
});
