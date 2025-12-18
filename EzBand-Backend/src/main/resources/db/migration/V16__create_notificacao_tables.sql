-- Tabela NOTIFICACAO (SINGLE_TABLE inheritance)
CREATE TABLE IF NOT EXISTS notificacao (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(100),
    mensagem TEXT,
    data_criacao TIMESTAMP,
    lida BOOLEAN DEFAULT FALSE,
    permite_resposta BOOLEAN DEFAULT TRUE,
    remetente_id BIGINT,
    remetente_tipo VARCHAR(50),
    destinatario_id BIGINT,
    destinatario_tipo VARCHAR(50),
    url_imagem VARCHAR(1000),
    titulo VARCHAR(255),
    -- Campos específicos de ConviteParaEvento
    id_evento_convite BIGINT,
    tipo_evento_convite VARCHAR(50),
    id_usuario_convidado BIGINT,
    cache_convidado DECIMAL(19, 2),
    instrumentos_convidado VARCHAR(500),
    -- Campos específicos de SolicitacaoAgendarEnsaio
    id_ensaio_solicitacao BIGINT,
    id_banda_solicitacao BIGINT,
    -- Campos específicos de SolicitacaoAgendarShow
    id_show_solicitacao BIGINT,
    -- Campos específicos de ConviteParaUsuarioIngressarBanda
    id_banda_convite BIGINT,
    id_usuario_convite BIGINT,
    -- Campos específicos de SolicitacaoParaIngressarBanda
    id_usuario_solicitacao BIGINT,
    -- Campos específicos de UsuarioExpulsoDeBanda
    id_banda_expulsao BIGINT,
    id_usuario_expulso BIGINT,
    nome_banda_expulsao VARCHAR(255)
);

-- Tabela RESPOSTA_NOTIFICACAO
CREATE TABLE IF NOT EXISTS resposta_notificacao (
    id BIGSERIAL PRIMARY KEY,
    notificacao_id BIGINT,
    acao VARCHAR(50),
    mensagem TEXT,
    data_resposta TIMESTAMP,
    CONSTRAINT fk_resposta_notificacao_notificacao FOREIGN KEY (notificacao_id) REFERENCES notificacao(id) ON DELETE CASCADE
);

-- Tabela ESTADO_NOTIFICACAO
CREATE TABLE IF NOT EXISTS estado_notificacao (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT,
    id_notificacao BIGINT,
    status_notificacao VARCHAR(50),
    mensagem_adicional VARCHAR(500),
    CONSTRAINT fk_estado_notificacao_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_estado_notificacao_notificacao FOREIGN KEY (id_notificacao) REFERENCES notificacao(id) ON DELETE CASCADE
);

CREATE INDEX idx_notificacao_tipo ON notificacao(tipo);
CREATE INDEX idx_notificacao_destinatario ON notificacao(destinatario_id);
CREATE INDEX idx_notificacao_remetente ON notificacao(remetente_id);
CREATE INDEX idx_notificacao_lida ON notificacao(lida);
CREATE INDEX idx_resposta_notificacao_notificacao ON resposta_notificacao(notificacao_id);
CREATE INDEX idx_estado_notificacao_usuario ON estado_notificacao(id_usuario);
CREATE INDEX idx_estado_notificacao_notificacao ON estado_notificacao(id_notificacao);
