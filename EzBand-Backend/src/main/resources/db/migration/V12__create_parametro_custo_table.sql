-- Tabela PARAMETRO_CUSTO
CREATE TABLE IF NOT EXISTS parametro_custo (
    id BIGSERIAL PRIMARY KEY,
    id_banda BIGINT,
    nome VARCHAR(255),
    descricao TEXT,
    unidade VARCHAR(100),
    valor_unitario DECIMAL(19, 2),
    tipo_calculo VARCHAR(50),
    CONSTRAINT fk_parametro_custo_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE
);

CREATE INDEX idx_parametro_custo_banda ON parametro_custo(id_banda);
