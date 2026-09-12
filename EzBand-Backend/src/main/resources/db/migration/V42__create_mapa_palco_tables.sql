-- Mapa de palco: cada registro e uma variacao completa da banda no palco
-- (quem toca, onde fica cada peca e o que cada um usa). Variacoes nascem por
-- duplicacao; id_derivado_de guarda a origem apenas para rastreio.
--
-- O palco e um desenho livre em escala real, em metros. E o que permite o
-- mesmo editor servir um power trio num bar e uma banda com naipe de metais:
-- muda o tamanho do palco, nao o tamanho dos simbolos. Tambem vira uma linha
-- do rider ("palco minimo").
CREATE TABLE IF NOT EXISTS mapa_palco (
    id BIGSERIAL PRIMARY KEY,
    id_banda BIGINT NOT NULL,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(500),
    padrao BOOLEAN NOT NULL DEFAULT FALSE,
    id_derivado_de BIGINT,
    largura_m NUMERIC(5,2) NOT NULL DEFAULT 8,
    profundidade_m NUMERIC(5,2) NOT NULL DEFAULT 6,
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


-- Posicao: um musico no palco ("Guitarra 1"), nao uma pessoa especifica. O
-- vinculo com usuario ou membro fantasma e opcional, para o mapa sobreviver a
-- troca de integrante e servir a um musico substituto.
--
-- pos_x/pos_y sao percentuais das dimensoes do palco (0,0 = fundo a esquerda;
-- 100,100 = frente a direita). Posicionamento livre nos dois eixos.
CREATE TABLE IF NOT EXISTS posicao_palco (
    id BIGSERIAL PRIMARY KEY,
    id_mapa_palco BIGINT NOT NULL,
    rotulo VARCHAR(80) NOT NULL,
    instrumento VARCHAR(100),
    modelo VARCHAR(50) NOT NULL DEFAULT 'musico-em-pe',
    pos_x NUMERIC(6,2) NOT NULL DEFAULT 50,
    pos_y NUMERIC(6,2) NOT NULL DEFAULT 50,
    escala NUMERIC(4,2) NOT NULL DEFAULT 1,
    rotacao NUMERIC(5,2) NOT NULL DEFAULT 0,
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


-- Item tecnico. Guarda DUAS camadas de propositos diferentes:
--
--   tipo   = o que e, para o rider somar (AMPLIFICADOR, TOMADA, MONITOR_WEDGE).
--   modelo = o que foi arrastado da paleta, para desenhar e dimensionar
--            ("combo-2x12", "cabecote-4x12", "bateria-5-pecas").
--
-- Sem essa separacao ou o rider perde precisao ou a paleta fica pobre: existem
-- muitos amplificadores diferentes e todos somam a mesma coisa no rider.
--
-- Item COM modelo e uma peca desenhada no palco e tem pos_x/pos_y.
-- Item SEM modelo e um atributo invisivel do dono -- "microfonacao: 8 mics da
-- casa", "2 tomadas 220V". Ninguem quer arrastar oito icones de microfone para
-- microfonar uma bateria, e mesmo assim o rider precisa contar os oito.
--
-- id_posicao NULO significa peca do palco sem dono (PA, mesa, pratica vel).
CREATE TABLE IF NOT EXISTS item_mapa_palco (
    id BIGSERIAL PRIMARY KEY,
    id_mapa_palco BIGINT NOT NULL,
    id_posicao BIGINT,
    tipo VARCHAR(40) NOT NULL,
    modelo VARCHAR(50),
    origem VARCHAR(20) NOT NULL DEFAULT 'PROPRIO',
    quantidade INTEGER NOT NULL DEFAULT 1,
    rotulo VARCHAR(80),
    marca_modelo VARCHAR(150),
    observacao VARCHAR(500),
    ordem INTEGER NOT NULL DEFAULT 0,
    -- Quantos canais de mesa o item consome. Pre-preenchido pelo modelo e
    -- editavel: bateria vale 6-8 canais, teclado estereo vale 2, e um
    -- microfone que apenas capta um amplificador ja contado vale 0.
    canais INTEGER NOT NULL DEFAULT 0,
    voltagem INTEGER,
    vias INTEGER,
    mix_independente BOOLEAN,
    ponto VARCHAR(100),
    pos_x NUMERIC(6,2),
    pos_y NUMERIC(6,2),
    escala NUMERIC(4,2),
    rotacao NUMERIC(5,2),
    CONSTRAINT fk_item_mapa_palco_mapa FOREIGN KEY (id_mapa_palco) REFERENCES mapa_palco(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_mapa_palco_posicao FOREIGN KEY (id_posicao) REFERENCES posicao_palco(id) ON DELETE CASCADE,
    CONSTRAINT ck_item_mapa_palco_quantidade CHECK (quantidade > 0)
);

CREATE INDEX IF NOT EXISTS idx_item_mapa_palco_mapa ON item_mapa_palco(id_mapa_palco);
CREATE INDEX IF NOT EXISTS idx_item_mapa_palco_posicao ON item_mapa_palco(id_posicao);
