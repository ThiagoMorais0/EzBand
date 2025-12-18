-- Tabela LOCAL_EVENTO
CREATE TABLE IF NOT EXISTS local_evento (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT,
    nome VARCHAR(255),
    bio TEXT,
    url_foto_perfil VARCHAR(1000),
    horario_inicio_funcionamento TIMESTAMP,
    horario_final_funcionamento TIMESTAMP,
    data_inclusao TIMESTAMP,
    -- Campos do Endereco (Embedded)
    pais VARCHAR(255),
    estado VARCHAR(255),
    cidade VARCHAR(255),
    bairro VARCHAR(255),
    rua VARCHAR(255),
    numero VARCHAR(50),
    cep VARCHAR(20),
    complemento VARCHAR(255),
    CONSTRAINT fk_local_evento_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
);

CREATE INDEX idx_local_evento_usuario ON local_evento(id_usuario);
CREATE INDEX idx_local_evento_nome ON local_evento(nome);
