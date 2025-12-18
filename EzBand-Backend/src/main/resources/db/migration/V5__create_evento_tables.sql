-- Tabela EVENTO (Superclasse com JOINED inheritance)
CREATE TABLE IF NOT EXISTS evento (
    id BIGSERIAL PRIMARY KEY,
    id_banda BIGINT,
    data_inclusao DATE,
    data DATE,
    duracao TIME,
    horario_inicio TIME,
    -- Campos do Endereco (Embedded)
    pais VARCHAR(255),
    estado VARCHAR(255),
    cidade VARCHAR(255),
    bairro VARCHAR(255),
    rua VARCHAR(255),
    numero VARCHAR(50),
    cep VARCHAR(20),
    complemento VARCHAR(255),
    local VARCHAR(500),
    observacoes TEXT,
    status VARCHAR(50),
    tipo_evento VARCHAR(50),
    CONSTRAINT fk_evento_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE
);

CREATE INDEX idx_evento_banda ON evento(id_banda);
CREATE INDEX idx_evento_data ON evento(data);
CREATE INDEX idx_evento_tipo ON evento(tipo_evento);
CREATE INDEX idx_evento_status ON evento(status);
