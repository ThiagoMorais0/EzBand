-- Tabela AVALIACAO_EVENTO
-- Armazena avaliações de eventos (shows e ensaios) feitas pela banda
CREATE TABLE IF NOT EXISTS avaliacao_evento (
    id BIGSERIAL PRIMARY KEY,
    id_evento BIGINT NOT NULL,
    tipo_evento VARCHAR(20) NOT NULL,
    id_banda BIGINT NOT NULL,
    id_usuario_avaliador BIGINT NOT NULL,
    
    -- Avaliação do local/estúdio (1 a 5)
    qualidade_som INTEGER,
    estrutura INTEGER,
    organizacao INTEGER,
    atendimento INTEGER,
    
    -- Avaliação geral do evento (1 a 5)
    experiencia_geral INTEGER NOT NULL,
    
    -- Comentários opcionais
    comentario_positivo TEXT,
    comentario_negativo TEXT,
    observacoes TEXT,
    
    data_avaliacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_avaliacao_evento_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_evento_usuario FOREIGN KEY (id_usuario_avaliador) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT chk_qualidade_som CHECK (qualidade_som IS NULL OR (qualidade_som >= 1 AND qualidade_som <= 5)),
    CONSTRAINT chk_estrutura CHECK (estrutura IS NULL OR (estrutura >= 1 AND estrutura <= 5)),
    CONSTRAINT chk_organizacao CHECK (organizacao IS NULL OR (organizacao >= 1 AND organizacao <= 5)),
    CONSTRAINT chk_atendimento CHECK (atendimento IS NULL OR (atendimento >= 1 AND atendimento <= 5)),
    CONSTRAINT chk_experiencia_geral CHECK (experiencia_geral >= 1 AND experiencia_geral <= 5)
);

-- Índices para melhorar performance
CREATE INDEX idx_avaliacao_evento_banda ON avaliacao_evento(id_banda);
CREATE INDEX idx_avaliacao_evento_evento ON avaliacao_evento(id_evento, tipo_evento);
CREATE INDEX idx_avaliacao_evento_usuario ON avaliacao_evento(id_usuario_avaliador);
CREATE INDEX idx_avaliacao_evento_data ON avaliacao_evento(data_avaliacao);

-- Constraint para evitar múltiplas avaliações do mesmo evento pela mesma banda
CREATE UNIQUE INDEX idx_avaliacao_evento_unique ON avaliacao_evento(id_evento, tipo_evento, id_banda);

-- Adicionar campo de comentário nas tabelas de avaliação existentes
ALTER TABLE avaliacao_estudio ADD COLUMN IF NOT EXISTS comentario TEXT;
ALTER TABLE avaliacao_local_evento ADD COLUMN IF NOT EXISTS comentario TEXT;
