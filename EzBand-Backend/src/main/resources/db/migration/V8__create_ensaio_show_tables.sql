-- Tabela ENSAIO (Herança JOINED de EVENTO)
CREATE TABLE IF NOT EXISTS ensaio (
    id BIGINT PRIMARY KEY,
    id_estudio BIGINT,
    valor DECIMAL(19, 2),
    CONSTRAINT fk_ensaio_evento FOREIGN KEY (id) REFERENCES evento(id) ON DELETE CASCADE,
    CONSTRAINT fk_ensaio_estudio FOREIGN KEY (id_estudio) REFERENCES estudio(id) ON DELETE SET NULL
);

-- Tabela SHOW (Herança JOINED de EVENTO)
CREATE TABLE IF NOT EXISTS show (
    id BIGINT PRIMARY KEY,
    id_local_evento BIGINT,
    horario_passagem_som TIME,
    valor_contrato DECIMAL(19, 2),
    is_portaria BOOLEAN,
    porcentagem_portaria INTEGER,
    consumacao_por_musico DECIMAL(19, 2),
    CONSTRAINT fk_show_evento FOREIGN KEY (id) REFERENCES evento(id) ON DELETE CASCADE,
    CONSTRAINT fk_show_local_evento FOREIGN KEY (id_local_evento) REFERENCES local_evento(id) ON DELETE SET NULL
);

CREATE INDEX idx_ensaio_estudio ON ensaio(id_estudio);
CREATE INDEX idx_show_local_evento ON show(id_local_evento);
