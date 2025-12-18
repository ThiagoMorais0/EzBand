-- Tabela ORCAMENTO
CREATE TABLE IF NOT EXISTS orcamento (
    id BIGSERIAL PRIMARY KEY,
    id_evento BIGINT,
    id_banda BIGINT,
    data_geracao TIMESTAMP,
    valor_total DECIMAL(19, 2),
    CONSTRAINT fk_orcamento_evento FOREIGN KEY (id_evento) REFERENCES show(id) ON DELETE CASCADE,
    CONSTRAINT fk_orcamento_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE
);

-- Tabela ITEM_ORCAMENTO
CREATE TABLE IF NOT EXISTS item_orcamento (
    id BIGSERIAL PRIMARY KEY,
    id_orcamento BIGINT,
    nome_parametro VARCHAR(255),
    unidade VARCHAR(100),
    valor_unitario DECIMAL(19, 2),
    valor_customizado DECIMAL(19, 2),
    quantidade DECIMAL(19, 2),
    subtotal DECIMAL(19, 2),
    CONSTRAINT fk_item_orcamento_orcamento FOREIGN KEY (id_orcamento) REFERENCES orcamento(id) ON DELETE CASCADE
);

-- Tabela CONDICAO_ORCAMENTO
CREATE TABLE IF NOT EXISTS condicao_orcamento (
    id BIGSERIAL PRIMARY KEY,
    id_banda BIGINT,
    id_orcamento BIGINT,
    condicao TEXT,
    CONSTRAINT fk_condicao_orcamento_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE,
    CONSTRAINT fk_condicao_orcamento_orcamento FOREIGN KEY (id_orcamento) REFERENCES orcamento(id) ON DELETE CASCADE
);

CREATE INDEX idx_orcamento_evento ON orcamento(id_evento);
CREATE INDEX idx_orcamento_banda ON orcamento(id_banda);
CREATE INDEX idx_item_orcamento_orcamento ON item_orcamento(id_orcamento);
CREATE INDEX idx_condicao_orcamento_banda ON condicao_orcamento(id_banda);
CREATE INDEX idx_condicao_orcamento_orcamento ON condicao_orcamento(id_orcamento);
