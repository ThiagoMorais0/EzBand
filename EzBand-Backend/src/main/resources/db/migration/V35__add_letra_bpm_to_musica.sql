-- Adicionar campos LETRA e BPM ao embeddable Musica (repertorio da banda e dos eventos)

ALTER TABLE repertorio_banda
ADD COLUMN IF NOT EXISTS letra TEXT;

ALTER TABLE repertorio_banda
ADD COLUMN IF NOT EXISTS bpm INTEGER;

ALTER TABLE repertorio_evento
ADD COLUMN IF NOT EXISTS letra TEXT;

ALTER TABLE repertorio_evento
ADD COLUMN IF NOT EXISTS bpm INTEGER;

COMMENT ON COLUMN repertorio_banda.letra IS 'Letra da música';
COMMENT ON COLUMN repertorio_banda.bpm IS 'Andamento da música em batidas por minuto';
COMMENT ON COLUMN repertorio_evento.letra IS 'Letra da música';
COMMENT ON COLUMN repertorio_evento.bpm IS 'Andamento da música em batidas por minuto';
