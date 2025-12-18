-- Tabela PENDENCIA_AVALIACAO_EVENTO
-- Registra pendências de avaliação de eventos (shows e ensaios) para cada banda
CREATE TABLE IF NOT EXISTS pendencia_avaliacao_evento (
    id BIGSERIAL PRIMARY KEY,
    id_evento BIGINT NOT NULL,
    tipo_evento VARCHAR(20) NOT NULL,
    id_banda BIGINT NOT NULL,
    data_evento DATE NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_adiamento TIMESTAMP,
    quantidade_adiamentos INTEGER DEFAULT 0,
    avaliado BOOLEAN DEFAULT FALSE,
    data_avaliacao TIMESTAMP,
    CONSTRAINT fk_pendencia_avaliacao_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE
);

-- Índices para melhorar performance
CREATE INDEX idx_pendencia_avaliacao_banda ON pendencia_avaliacao_evento(id_banda);
CREATE INDEX idx_pendencia_avaliacao_evento ON pendencia_avaliacao_evento(id_evento, tipo_evento);
CREATE INDEX idx_pendencia_avaliacao_avaliado ON pendencia_avaliacao_evento(avaliado);
CREATE INDEX idx_pendencia_avaliacao_data_evento ON pendencia_avaliacao_evento(data_evento);

-- Constraint para evitar duplicatas
CREATE UNIQUE INDEX idx_pendencia_avaliacao_unique ON pendencia_avaliacao_evento(id_evento, tipo_evento, id_banda) 
WHERE avaliado = FALSE;
