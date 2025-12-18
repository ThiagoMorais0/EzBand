-- Tabela ESTUDIO
CREATE TABLE IF NOT EXISTS estudio (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT,
    nome VARCHAR(255),
    descricao TEXT,
    horario_inicio_funcionamento TIMESTAMP,
    horario_final_funcionamento TIMESTAMP,
    data_inclusao TIMESTAMP,
    url_foto_perfil VARCHAR(1000),
    -- Campos do Endereco (Embedded)
    pais VARCHAR(255),
    estado VARCHAR(255),
    cidade VARCHAR(255),
    bairro VARCHAR(255),
    rua VARCHAR(255),
    numero VARCHAR(50),
    cep VARCHAR(20),
    complemento VARCHAR(255),
    CONSTRAINT fk_estudio_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
);

CREATE INDEX idx_estudio_usuario ON estudio(id_usuario);
CREATE INDEX idx_estudio_nome ON estudio(nome);
