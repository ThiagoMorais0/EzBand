-- Tabela MUSICO_BANDA (ID Composto)
CREATE TABLE IF NOT EXISTS musico_banda (
    id_usuario BIGINT NOT NULL,
    id_banda BIGINT NOT NULL,
    instrumentos VARCHAR(500),
    PRIMARY KEY (id_usuario, id_banda),
    CONSTRAINT fk_musico_banda_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_musico_banda_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE
);

-- Tabela de permissões do músico na banda (ElementCollection)
CREATE TABLE IF NOT EXISTS permissao_musico_banda (
    id_usuario BIGINT NOT NULL,
    id_banda BIGINT NOT NULL,
    permissoes VARCHAR(100),
    CONSTRAINT fk_permissao_musico_banda FOREIGN KEY (id_usuario, id_banda) REFERENCES musico_banda(id_usuario, id_banda) ON DELETE CASCADE
);

CREATE INDEX idx_musico_banda_usuario ON musico_banda(id_usuario);
CREATE INDEX idx_musico_banda_banda ON musico_banda(id_banda);
