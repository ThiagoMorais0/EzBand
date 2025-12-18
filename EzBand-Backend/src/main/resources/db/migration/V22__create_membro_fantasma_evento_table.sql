-- Tabela MEMBRO_FANTASMA_EVENTO
-- Vincula membros fantasma a eventos para controle de participação
CREATE TABLE IF NOT EXISTS membro_fantasma_evento (
    id_evento BIGINT NOT NULL,
    id_membro_fantasma BIGINT NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    instrumentos VARCHAR(255),
    cache DECIMAL(10, 2),
    PRIMARY KEY (id_evento, id_membro_fantasma, tipo_evento),
    CONSTRAINT fk_membro_fantasma_evento_evento
        FOREIGN KEY (id_evento)
            REFERENCES evento(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_membro_fantasma_evento_membro
        FOREIGN KEY (id_membro_fantasma)
            REFERENCES membro_fantasma(id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_membro_fantasma_evento_evento
    ON membro_fantasma_evento(id_evento, tipo_evento);

CREATE INDEX IF NOT EXISTS idx_membro_fantasma_evento_membro
    ON membro_fantasma_evento(id_membro_fantasma);
