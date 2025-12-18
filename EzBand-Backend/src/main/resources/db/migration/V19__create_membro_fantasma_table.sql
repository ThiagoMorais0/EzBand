-- Tabela MEMBRO_FANTASMA
CREATE TABLE IF NOT EXISTS membro_fantasma (
                                               id BIGSERIAL PRIMARY KEY,
                                               id_banda BIGINT NOT NULL,
                                               nome VARCHAR(255) NOT NULL,
                                               instrumento VARCHAR(255),
                                               url_foto VARCHAR(1000),
                                               observacoes VARCHAR(1000),
                                               CONSTRAINT fk_membro_fantasma_banda
                                                   FOREIGN KEY (id_banda)
                                                       REFERENCES banda(id)
                                                       ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_membro_fantasma_banda
    ON membro_fantasma(id_banda);
