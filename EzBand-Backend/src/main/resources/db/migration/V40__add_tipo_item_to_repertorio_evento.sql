-- Momentos do show: o repertório do evento deixa de ser uma lista só de músicas e passa a ser
-- uma lista de itens. Um MOMENTO é uma parada não-musical do roteiro ("dar boa noite",
-- "pausa para água") e reaproveita as colunas que já existem: `titulo` guarda o texto e
-- `duracao` a estimativa de quanto ele leva. As demais colunas ficam nulas.
--
-- O default MUSICA é o que mantém todo repertório já cadastrado válido sem backfill.
ALTER TABLE repertorio_evento
    ADD COLUMN IF NOT EXISTS tipo_item VARCHAR(20) NOT NULL DEFAULT 'MUSICA';

-- O Modo Palco filtra por esta coluna para montar o ponteiro da sessão (só músicas contam
-- como faixa), e é a consulta mais quente do show.
CREATE INDEX IF NOT EXISTS idx_repertorio_evento_tipo_item
    ON repertorio_evento (id_evento, tipo_evento, tipo_item);
