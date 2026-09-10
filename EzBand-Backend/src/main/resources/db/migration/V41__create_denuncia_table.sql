-- Tabela DENUNCIA: denuncias de perfis de usuarios e de bandas.
-- Apenas um dos alvos (id_usuario_denunciado / id_banda_denunciada) e preenchido,
-- conforme o tipo_alvo informado.
CREATE TABLE IF NOT EXISTS denuncia (
    id BIGSERIAL PRIMARY KEY,
    id_usuario_denunciante BIGINT NOT NULL,
    tipo_alvo VARCHAR(20) NOT NULL,
    id_usuario_denunciado BIGINT,
    id_banda_denunciada BIGINT,
    categoria VARCHAR(40) NOT NULL,
    descricao TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_denuncia_denunciante FOREIGN KEY (id_usuario_denunciante) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_denuncia_usuario_denunciado FOREIGN KEY (id_usuario_denunciado) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_denuncia_banda_denunciada FOREIGN KEY (id_banda_denunciada) REFERENCES banda(id) ON DELETE CASCADE,
    CONSTRAINT ck_denuncia_alvo CHECK (
        (tipo_alvo = 'USUARIO' AND id_usuario_denunciado IS NOT NULL AND id_banda_denunciada IS NULL)
        OR (tipo_alvo = 'BANDA' AND id_banda_denunciada IS NOT NULL AND id_usuario_denunciado IS NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_denuncia_denunciante ON denuncia(id_usuario_denunciante);
CREATE INDEX IF NOT EXISTS idx_denuncia_usuario_denunciado ON denuncia(id_usuario_denunciado);
CREATE INDEX IF NOT EXISTS idx_denuncia_banda_denunciada ON denuncia(id_banda_denunciada);
CREATE INDEX IF NOT EXISTS idx_denuncia_status ON denuncia(status);

-- Impede que o mesmo usuario denuncie o mesmo perfil mais de uma vez.
CREATE UNIQUE INDEX IF NOT EXISTS uk_denuncia_usuario
    ON denuncia(id_usuario_denunciante, id_usuario_denunciado)
    WHERE id_usuario_denunciado IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uk_denuncia_banda
    ON denuncia(id_usuario_denunciante, id_banda_denunciada)
    WHERE id_banda_denunciada IS NOT NULL;
