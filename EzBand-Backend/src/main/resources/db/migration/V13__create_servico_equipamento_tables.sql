-- Tabela SERVICO_ESTUDIO
CREATE TABLE IF NOT EXISTS servico_estudio (
    id BIGSERIAL PRIMARY KEY,
    id_estudio BIGINT,
    nome VARCHAR(255),
    descricao TEXT,
    valor DECIMAL(19, 2),
    CONSTRAINT fk_servico_estudio_estudio FOREIGN KEY (id_estudio) REFERENCES estudio(id) ON DELETE CASCADE
);

-- Tabela EQUIPAMENTO_ESTUDIO
CREATE TABLE IF NOT EXISTS equipamento_estudio (
    id BIGSERIAL PRIMARY KEY,
    id_estudio BIGINT,
    marca VARCHAR(255),
    modelo VARCHAR(255),
    observacao TEXT,
    ativo BOOLEAN,
    quantidade INTEGER,
    CONSTRAINT fk_equipamento_estudio_estudio FOREIGN KEY (id_estudio) REFERENCES estudio(id) ON DELETE CASCADE
);

-- Tabela EQUIPAMENTO_LOCAL_EVENTO
CREATE TABLE IF NOT EXISTS equipamento_local_evento (
    id BIGSERIAL PRIMARY KEY,
    id_local_evento BIGINT,
    marca VARCHAR(255),
    modelo VARCHAR(255),
    observacao TEXT,
    ativo BOOLEAN,
    quantidade INTEGER,
    CONSTRAINT fk_equipamento_local_evento_local FOREIGN KEY (id_local_evento) REFERENCES local_evento(id) ON DELETE CASCADE
);

CREATE INDEX idx_servico_estudio_estudio ON servico_estudio(id_estudio);
CREATE INDEX idx_equipamento_estudio_estudio ON equipamento_estudio(id_estudio);
CREATE INDEX idx_equipamento_local_evento_local ON equipamento_local_evento(id_local_evento);
