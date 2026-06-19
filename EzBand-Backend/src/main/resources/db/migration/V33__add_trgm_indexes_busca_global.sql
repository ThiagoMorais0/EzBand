CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_usuario_nome_trgm      ON usuario      USING GIN (nome gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_banda_nome_trgm        ON banda        USING GIN (nome gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_estudio_nome_trgm      ON estudio      USING GIN (nome gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_local_evento_nome_trgm ON local_evento USING GIN (nome gin_trgm_ops);
