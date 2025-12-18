-- Tabela BANDA
CREATE TABLE IF NOT EXISTS banda (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(255),
    data_inclusao DATE,
    url_logo VARCHAR(1000),
    -- Campos do Endereco (Embedded)
    pais VARCHAR(255),
    estado VARCHAR(255),
    cidade VARCHAR(255),
    bairro VARCHAR(255),
    rua VARCHAR(255),
    numero VARCHAR(50),
    cep VARCHAR(20),
    complemento VARCHAR(255),
    -- Campos do ParametrosBanda (Embedded)
    permite_entrada_por_convite BOOLEAN DEFAULT TRUE,
    exigir_aprovacao_compromissos BOOLEAN DEFAULT TRUE,
    listar_observacao_repertorio BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_banda_nome ON banda(nome);
CREATE INDEX idx_banda_categoria ON banda(categoria);
