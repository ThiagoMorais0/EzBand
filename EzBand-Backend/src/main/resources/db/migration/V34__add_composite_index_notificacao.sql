-- Substitui os índices separados por um composto otimizado para a query SSE:
-- WHERE destinatario_id = ? AND destinatario_tipo = ? AND lida = false
CREATE INDEX IF NOT EXISTS idx_notificacao_destinatario_composto
    ON notificacao (destinatario_id, destinatario_tipo, lida);
