-- Tabela ITEM_CHECKLIST_USUARIO
CREATE TABLE IF NOT EXISTS item_checklist_usuario (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(1000),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_item_checklist_usuario
        FOREIGN KEY (id_usuario)
            REFERENCES usuario(id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_item_checklist_usuario_usuario
    ON item_checklist_usuario(id_usuario);

CREATE INDEX IF NOT EXISTS idx_item_checklist_usuario_ativo
    ON item_checklist_usuario(ativo);

-- Tabela ITEM_CHECKLIST_BANDA
CREATE TABLE IF NOT EXISTS item_checklist_banda (
    id BIGSERIAL PRIMARY KEY,
    id_banda BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(1000),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_item_checklist_banda
        FOREIGN KEY (id_banda)
            REFERENCES banda(id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_item_checklist_banda_banda
    ON item_checklist_banda(id_banda);

CREATE INDEX IF NOT EXISTS idx_item_checklist_banda_ativo
    ON item_checklist_banda(ativo);
