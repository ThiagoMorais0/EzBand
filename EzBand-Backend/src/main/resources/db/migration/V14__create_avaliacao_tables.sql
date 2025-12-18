-- Tabela AVALIACAO_ESTUDIO
CREATE TABLE IF NOT EXISTS avaliacao_estudio (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT,
    id_estudio BIGINT,
    qualidade_som INTEGER,
    estrutura INTEGER,
    organizacao INTEGER,
    atendimento INTEGER,
    experiencia_geral INTEGER,
    data_avaliacao DATE,
    CONSTRAINT fk_avaliacao_estudio_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_estudio_estudio FOREIGN KEY (id_estudio) REFERENCES estudio(id) ON DELETE CASCADE
);

-- Tabela AVALIACAO_LOCAL_EVENTO
CREATE TABLE IF NOT EXISTS avaliacao_local_evento (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT,
    id_local_evento BIGINT,
    qualidade_som INTEGER,
    estrutura INTEGER,
    organizacao INTEGER,
    atendimento INTEGER,
    experiencia_geral INTEGER,
    data_avaliacao DATE,
    CONSTRAINT fk_avaliacao_local_evento_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_local_evento_local FOREIGN KEY (id_local_evento) REFERENCES local_evento(id) ON DELETE CASCADE
);

CREATE INDEX idx_avaliacao_estudio_usuario ON avaliacao_estudio(id_usuario);
CREATE INDEX idx_avaliacao_estudio_estudio ON avaliacao_estudio(id_estudio);
CREATE INDEX idx_avaliacao_local_evento_usuario ON avaliacao_local_evento(id_usuario);
CREATE INDEX idx_avaliacao_local_evento_local ON avaliacao_local_evento(id_local_evento);
