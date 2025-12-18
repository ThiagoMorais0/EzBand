-- Tabela MUSICO_EVENTO (ID Composto)
CREATE TABLE IF NOT EXISTS musico_evento (
    id_evento BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    instrumentos VARCHAR(500),
    cache DECIMAL(19, 2),
    situacao VARCHAR(50),
    PRIMARY KEY (id_evento, id_usuario, tipo_evento),
    CONSTRAINT fk_musico_evento_evento FOREIGN KEY (id_evento) REFERENCES evento(id) ON DELETE CASCADE,
    CONSTRAINT fk_musico_evento_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE INDEX idx_musico_evento_evento ON musico_evento(id_evento);
CREATE INDEX idx_musico_evento_usuario ON musico_evento(id_usuario);
