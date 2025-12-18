-- Tabela USUARIO
CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    celular VARCHAR(20),
    cidade VARCHAR(255),
    descricao TEXT,
    senha VARCHAR(255),
    data_nascimento DATE,
    data_criacao DATE,
    ativo BOOLEAN DEFAULT TRUE,
    bloqueado BOOLEAN DEFAULT FALSE,
    permissao VARCHAR(50),
    url_foto_perfil VARCHAR(1000)
);

CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_ativo ON usuario(ativo);
