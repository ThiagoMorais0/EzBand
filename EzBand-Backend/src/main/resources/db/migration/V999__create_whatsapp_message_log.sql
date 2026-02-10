-- Tabela para auditoria e rastreabilidade de mensagens WhatsApp
CREATE TABLE IF NOT EXISTS whatsapp_message_log (
    id BIGSERIAL PRIMARY KEY,
    destino VARCHAR(20) NOT NULL,
    conteudo TEXT,
    provider VARCHAR(50) NOT NULL,
    message_id VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    data_envio TIMESTAMP NOT NULL,
    erro TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhorar performance de consultas
CREATE INDEX idx_whatsapp_log_destino ON whatsapp_message_log(destino);
CREATE INDEX idx_whatsapp_log_message_id ON whatsapp_message_log(message_id);
CREATE INDEX idx_whatsapp_log_status ON whatsapp_message_log(status);
CREATE INDEX idx_whatsapp_log_provider ON whatsapp_message_log(provider);
CREATE INDEX idx_whatsapp_log_data_envio ON whatsapp_message_log(data_envio);

-- Comentários para documentação
COMMENT ON TABLE whatsapp_message_log IS 'Log de todas as mensagens WhatsApp enviadas pelo sistema';
COMMENT ON COLUMN whatsapp_message_log.destino IS 'Número de telefone no formato E.164 (+5511999999999)';
COMMENT ON COLUMN whatsapp_message_log.provider IS 'Provider usado (EVOLUTION_API ou CLOUD_API)';
COMMENT ON COLUMN whatsapp_message_log.message_id IS 'ID da mensagem retornado pela API';
COMMENT ON COLUMN whatsapp_message_log.status IS 'Status do envio: ENVIADO, ENTREGUE, LIDO, FALHA, PENDENTE';
