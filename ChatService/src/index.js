require('dotenv').config();
const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const mongoose = require('mongoose');
const cors = require('cors');
const path = require('path');
const jwt = require('jsonwebtoken');
const { registerUser, sendMessage } = require('./services/socketService');

const app = express();
app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, '../public')));

const server = http.createServer(app);
const io = new Server(server, {
    cors: { origin: '*' }
});

const uri = `mongodb://${process.env.MONGO_INITDB_ROOT_USERNAME}:${process.env.MONGO_INITDB_ROOT_PASSWORD}@mongodb:27017/${process.env.MONGO_DB_DATABASE}?authSource=admin`;
// 🔹 Conexão com MongoDB
mongoose.connect(uri)
    .then(() => console.log('✅ MongoDB conectado'))
    .catch(err => console.error('❌ Erro ao conectar MongoDB:', err));

const JWT_SECRET = process.env.JWT_SECRET;
// Middleware para autenticar JWT no handshake
io.use((socket, next) => {
    const token = socket.handshake.auth?.token || socket.handshake.headers?.token;

    if (!token) {
        return next(new Error("Token ausente"));
    }

    try {
        const decoded = jwt.verify(token, JWT_SECRET);
        socket.user = decoded; // anexamos info do usuário ao socket
        next();
    } catch (err) {
        console.error("Token inválido:", err.message);
        next(new Error("Token inválido"));
    }
});

// conexão de usuários
io.on('connection', (socket) => {
    console.log('Novo socket conectado:', socket.id);

    // usuário se registra
    socket.on('register', (user) => {
        // user = { id, type }
        registerUser(socket, user);
        console.log(`Usuário registrado: ${user.type}:${user.id}`);
    });

    // mensagem 1x1
    socket.on('sendMessage', (msg) => {
        // msg = { sender: {id, type}, receiver: {id, type}, text }
        console.log('Mensagem recebida do cliente:', msg);
        sendMessage(msg);
    });

    socket.on('disconnect', () => {
        console.log('Socket desconectado:', socket.id);
    });
});

server.listen(3001, () => {
    console.log('Servidor rodando em http://localhost:3001');
});
