-- VS (virtual soundcheck) da musica: o arquivo que o baterista toca no fone com
-- click de um lado e playback do outro. Guarda o objeto no MinIO; aqui fica so o
-- ponteiro e o que o app precisa para listar sem ir ao storage.
--
-- Nao e coluna de repertorio_banda nem de repertorio_evento porque `Musica` e um
-- @Embeddable copiado por valor: a mesma musica existe uma vez no repertorio da
-- banda e mais uma por evento que a toque. Uma coluna em cada copia significaria
-- N ponteiros para um arquivo so, e nenhuma forma de saber quando apagar.
--
-- O vinculo e (banda, chave_musica), onde chave_musica e titulo|artista
-- normalizado. E a unica identidade que o repertorio da banda e o do evento
-- compartilham -- repertorio_evento_id carrega id_banda, mas nao o id da linha de
-- repertorio_banda de onde a copia saiu.
CREATE TABLE IF NOT EXISTS audio_musica (
    id BIGSERIAL PRIMARY KEY,
    id_banda BIGINT NOT NULL,
    chave_musica VARCHAR(300) NOT NULL,
    titulo VARCHAR(200),
    artista VARCHAR(200),
    url VARCHAR(500) NOT NULL,
    nome_original VARCHAR(255),
    duracao_seg INTEGER,
    tamanho_bytes BIGINT NOT NULL DEFAULT 0,
    sha256 VARCHAR(64),
    id_usuario_upload BIGINT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audio_musica_banda FOREIGN KEY (id_banda) REFERENCES banda(id) ON DELETE CASCADE
);

-- Um VS por musica por banda. Trocar o arquivo sobrescreve a linha.
CREATE UNIQUE INDEX IF NOT EXISTS uk_audio_musica_banda_chave
    ON audio_musica(id_banda, chave_musica);

-- A tela de repertorio pede todos os audios da banda de uma vez e casa por chave
-- no cliente; e uma consulta so por tela, nao uma por musica.
CREATE INDEX IF NOT EXISTS idx_audio_musica_banda ON audio_musica(id_banda);

-- Dedup: o mesmo arquivo enviado para duas musicas reaproveita o objeto no
-- storage, entao apagar uma linha so pode apagar o objeto se nenhuma outra
-- apontar para o mesmo hash.
CREATE INDEX IF NOT EXISTS idx_audio_musica_sha256 ON audio_musica(sha256);
