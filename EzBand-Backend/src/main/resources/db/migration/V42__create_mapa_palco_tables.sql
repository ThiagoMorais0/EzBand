-- Mapa de palco: cada registro e uma variacao completa da banda no palco
-- (formacao + layout + equipamentos). Variacoes nascem por duplicacao;
-- id_derivado_de guarda a origem apenas para rastreio, sem vinculo funcional.
CREATE TABLE IF NOT EXISTS mapa_palco (
    id BIGSERIAL PRIMARY KEY,
    id_banda BIGINT NOT NULL,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(500),
    padrao BOOLEAN NOT NULL DEFAULT FALSE,
    id_derivado_de BIGINT,
    grade_colunas INTEGER NOT NULL DEFAULT 12,
    grade_linhas INTEGER NOT NULL DEFAULT 6,
    observacoes TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mapa_palco_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE,
    CONSTRAINT fk_mapa_palco_derivado FOREIGN KEY (id_derivado_de) REFERENCES mapa_palco(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_mapa_palco_banda ON mapa_palco(id_banda);

-- Uma unica variacao padrao por banda (a que aparece no perfil).
CREATE UNIQUE INDEX IF NOT EXISTS uk_mapa_palco_padrao
    ON mapa_palco(id_banda)
    WHERE padrao = TRUE AND ativo = TRUE;


-- Posicao: um lugar no palco ("Guitarra 1"), nao uma pessoa. O vinculo com
-- usuario ou membro fantasma e opcional, para o mapa sobreviver a troca de
-- integrante e servir a musico substituto.
CREATE TABLE IF NOT EXISTS posicao_palco (
    id BIGSERIAL PRIMARY KEY,
    id_mapa_palco BIGINT NOT NULL,
    rotulo VARCHAR(80) NOT NULL,
    instrumento VARCHAR(100),
    coluna INTEGER NOT NULL DEFAULT 0,
    linha INTEGER NOT NULL DEFAULT 0,
    largura_cel INTEGER NOT NULL DEFAULT 1,
    altura_cel INTEGER NOT NULL DEFAULT 1,
    id_usuario BIGINT,
    id_membro_fantasma BIGINT,
    backing_vocal BOOLEAN NOT NULL DEFAULT FALSE,
    ordem_canal INTEGER NOT NULL DEFAULT 0,
    observacao VARCHAR(500),
    -- Nulo enquanto a ficha tecnica nunca foi preenchida (badge de pendencia).
    data_preenchimento TIMESTAMP,
    CONSTRAINT fk_posicao_palco_mapa FOREIGN KEY (id_mapa_palco) REFERENCES mapa_palco(id) ON DELETE CASCADE,
    CONSTRAINT fk_posicao_palco_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL,
    CONSTRAINT fk_posicao_palco_fantasma FOREIGN KEY (id_membro_fantasma) REFERENCES membro_fantasma(id) ON DELETE SET NULL,
    CONSTRAINT ck_posicao_palco_ocupante CHECK (id_usuario IS NULL OR id_membro_fantasma IS NULL)
);

CREATE INDEX IF NOT EXISTS idx_posicao_palco_mapa ON posicao_palco(id_mapa_palco);
CREATE INDEX IF NOT EXISTS idx_posicao_palco_usuario ON posicao_palco(id_usuario);


-- Item tecnico. Tabela unica de proposito: a diferenca entre o wedge do
-- guitarrista e o praticavel da bateria e apenas de quem ele e --
-- id_posicao NULL significa item geral do palco (PA, mesa, tomada avulsa).
-- voltagem/vias/mix_independente/ponto sao tipados porque sao exatamente os
-- campos que o rider soma; o resto da variacao cabe em marca_modelo/observacao.
CREATE TABLE IF NOT EXISTS item_mapa_palco (
    id BIGSERIAL PRIMARY KEY,
    id_mapa_palco BIGINT NOT NULL,
    id_posicao BIGINT,
    tipo VARCHAR(40) NOT NULL,
    origem VARCHAR(20) NOT NULL DEFAULT 'PROPRIO',
    quantidade INTEGER NOT NULL DEFAULT 1,
    rotulo VARCHAR(80),
    marca_modelo VARCHAR(150),
    observacao VARCHAR(500),
    ordem INTEGER NOT NULL DEFAULT 0,
    -- Quantos canais de mesa o item consome. Pre-preenchido pelo tipo e
    -- editavel: bateria vale 6-8 canais, teclado estereo vale 2, e um
    -- microfone que apenas capta um amplificador ja contado vale 0.
    canais INTEGER NOT NULL DEFAULT 0,
    voltagem INTEGER,
    vias INTEGER,
    mix_independente BOOLEAN,
    ponto VARCHAR(100),
    -- Nulos = desenha derivado da posicao. Preenchidos = usuario ajustou a mao.
    coluna INTEGER,
    linha INTEGER,
    CONSTRAINT fk_item_mapa_palco_mapa FOREIGN KEY (id_mapa_palco) REFERENCES mapa_palco(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_mapa_palco_posicao FOREIGN KEY (id_posicao) REFERENCES posicao_palco(id) ON DELETE CASCADE,
    CONSTRAINT ck_item_mapa_palco_quantidade CHECK (quantidade > 0)
);

CREATE INDEX IF NOT EXISTS idx_item_mapa_palco_mapa ON item_mapa_palco(id_mapa_palco);
CREATE INDEX IF NOT EXISTS idx_item_mapa_palco_posicao ON item_mapa_palco(id_posicao);
