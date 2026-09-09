-- Flag de confirmacao do evento (show/ensaio). Um evento nao confirmado apenas
-- "segura" a data na agenda enquanto a negociacao com o contratante acontece.

ALTER TABLE evento
ADD COLUMN IF NOT EXISTS confirmado BOOLEAN DEFAULT TRUE;

-- Todos os eventos ja cadastrados sao considerados confirmados.
UPDATE evento SET confirmado = TRUE WHERE confirmado IS NULL;

COMMENT ON COLUMN evento.confirmado IS 'Data confirmada com o contratante; false = data apenas reservada durante negociacao';
