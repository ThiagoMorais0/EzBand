# Usar Node.js LTS
FROM node:latest

# Definir diretório de trabalho
WORKDIR /app

# Copiar package.json e package-lock.json (ou yarn.lock)
COPY package*.json ./

# Instalar dependências
RUN npm install --production

# Copiar todo o código da aplicação
COPY . .

# Expor a porta que o chat roda
EXPOSE 3001

# Comando para iniciar a aplicação
CMD ["node", "src/ws/wsServer.js"]