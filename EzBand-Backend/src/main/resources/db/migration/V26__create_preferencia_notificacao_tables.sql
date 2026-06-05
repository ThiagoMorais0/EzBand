CREATE TABLE IF NOT EXISTS preferencia_notificacao_membro (
    id BIGSERIAL PRIMARY KEY,
    id_banda BIGINT NOT NULL,
    id_usuario BIGINT,
    id_membro_fantasma BIGINT,
    notificar_novo_evento BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS dias_antecedencia_notificacao (
    id_preferencia BIGINT NOT NULL,
    dias INTEGER,
    CONSTRAINT fk_dias_preferencia
        FOREIGN KEY (id_preferencia)
        REFERENCES preferencia_notificacao_membro(id)
        ON DELETE CASCADE
);
