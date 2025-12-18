-- Tabela PUBLICACAO (SINGLE_TABLE inheritance)
CREATE TABLE IF NOT EXISTS publicacao (
    id BIGSERIAL PRIMARY KEY,
    tipo_publicacao VARCHAR(50),
    texto TEXT,
    data_inclusao TIMESTAMP,
    id_usuario BIGINT,
    id_banda BIGINT,
    CONSTRAINT fk_publicacao_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_publicacao_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE
);

-- Tabela IMAGEM_PUBLICACAO
CREATE TABLE IF NOT EXISTS imagem_publicacao (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(1000),
    id_publicacao BIGINT,
    CONSTRAINT fk_imagem_publicacao_publicacao FOREIGN KEY (id_publicacao) REFERENCES publicacao(id) ON DELETE CASCADE
);

CREATE INDEX idx_publicacao_tipo ON publicacao(tipo_publicacao);
CREATE INDEX idx_publicacao_usuario ON publicacao(id_usuario);
CREATE INDEX idx_publicacao_banda ON publicacao(id_banda);
CREATE INDEX idx_imagem_publicacao_publicacao ON imagem_publicacao(id_publicacao);
