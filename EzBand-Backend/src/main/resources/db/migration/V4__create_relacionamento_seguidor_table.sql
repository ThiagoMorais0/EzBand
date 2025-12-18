-- Tabela RELACIONAMENTO_SEGUIDOR (ID Composto)
CREATE TABLE IF NOT EXISTS relacionamento_seguidor (
    id_seguidor BIGINT NOT NULL,
    id_seguido BIGINT NOT NULL,
    status VARCHAR(50),
    data_solicitacao DATE,
    data_aceitacao DATE,
    PRIMARY KEY (id_seguidor, id_seguido),
    CONSTRAINT fk_relacionamento_seguidor FOREIGN KEY (id_seguidor) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_relacionamento_seguido FOREIGN KEY (id_seguido) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE INDEX idx_relacionamento_seguidor ON relacionamento_seguidor(id_seguidor);
CREATE INDEX idx_relacionamento_seguido ON relacionamento_seguidor(id_seguido);
