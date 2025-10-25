const jwt = require('jsonwebtoken');
const SECRET = process.env.JWT_SECRET;

const verifyToken = (token) => {
    return jwt.verify(token, SECRET);
};

module.exports = { verifyToken };
