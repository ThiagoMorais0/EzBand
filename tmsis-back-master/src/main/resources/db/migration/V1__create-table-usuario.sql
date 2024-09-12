-- Criando tabela Usuarios - Thiago Morais - 10/01/2024

CREATE TABLE IF NOT EXISTS usuario (
                          id SERIAL PRIMARY KEY,
                          nome VARCHAR(100) NOT NULL,
                          login VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          senha VARCHAR(255) NOT NULL,
                          data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          permissao VARCHAR(100) NOT NULL,
                          ativo BOOLEAN DEFAULT TRUE,
                          bloqueado BOOLEAN DEFAULT TRUE
);