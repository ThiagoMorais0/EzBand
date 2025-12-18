-- Tabela REPERTORIO_BANDA
CREATE TABLE IF NOT EXISTS repertorio_banda (
    id BIGSERIAL PRIMARY KEY,
    id_banda BIGINT,
    -- Campos do Musica (Embedded)
    titulo VARCHAR(255),
    artista VARCHAR(255),
    descricao TEXT,
    observacao TEXT,
    duracao TIME,
    tonalidade VARCHAR(50),
    url_youtube VARCHAR(500),
    url_spotify VARCHAR(500),
    CONSTRAINT fk_repertorio_banda_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE
);

-- Tabela REPERTORIO_EVENTO (ID Composto)
CREATE TABLE IF NOT EXISTS repertorio_evento (
    indice INTEGER NOT NULL,
    id_evento BIGINT NOT NULL,
    id_banda BIGINT NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    bloco VARCHAR(100),
    -- Campos do Musica (Embedded)
    titulo VARCHAR(255),
    artista VARCHAR(255),
    descricao TEXT,
    observacao TEXT,
    duracao TIME,
    tonalidade VARCHAR(50),
    url_youtube VARCHAR(500),
    url_spotify VARCHAR(500),
    PRIMARY KEY (indice, id_evento, id_banda, tipo_evento),
    CONSTRAINT fk_repertorio_evento_evento FOREIGN KEY (id_evento) REFERENCES evento(id) ON DELETE CASCADE,
    CONSTRAINT fk_repertorio_evento_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE
);

CREATE INDEX idx_repertorio_banda_banda ON repertorio_banda(id_banda);
CREATE INDEX idx_repertorio_evento_evento ON repertorio_evento(id_evento);
CREATE INDEX idx_repertorio_evento_banda ON repertorio_evento(id_banda);
