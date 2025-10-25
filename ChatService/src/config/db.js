const mongoose = require('mongoose');

const connectDB = async () => {
    const uri = `mongodb://${process.env.MONGO_INITDB_ROOT_USERNAME}:${process.env.MONGO_INITDB_ROOT_PASSWORD}@db_mongo:27017/${process.env.MONGO_DB_DATABASE}?authSource=admin`;
    console.log('uri: ' + uri)
    try {
        await mongoose.connect(uri);
        console.log('✅ MongoDB conectado');
    } catch (err) {
        console.error('❌ Erro ao conectar MongoDB:', err);
        process.exit(1);
    }
};

module.exports = connectDB;
