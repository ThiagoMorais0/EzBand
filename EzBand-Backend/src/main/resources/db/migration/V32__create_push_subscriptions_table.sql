CREATE TABLE IF NOT EXISTS push_subscriptions (
    id         BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT    NOT NULL,
    endpoint   TEXT      NOT NULL,
    p256dh     TEXT      NOT NULL,
    auth       TEXT      NOT NULL,
    criado_em  TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_push_sub_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT uq_push_endpoint    UNIQUE (endpoint)
);

CREATE INDEX idx_push_sub_usuario ON push_subscriptions(usuario_id);
