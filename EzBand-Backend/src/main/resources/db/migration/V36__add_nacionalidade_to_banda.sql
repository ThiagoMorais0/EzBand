-- Nacionalidade (pais de origem) da banda, em codigo ISO 3166-1 alpha-2 (ex: BR, US, AR).
-- Independente do endereco: a banda pode ensaiar em um pais e ser de outro.

ALTER TABLE banda
ADD COLUMN IF NOT EXISTS nacionalidade VARCHAR(2);

-- Todas as bandas ja cadastradas sao brasileiras.
UPDATE banda SET nacionalidade = 'BR' WHERE nacionalidade IS NULL;

COMMENT ON COLUMN banda.nacionalidade IS 'Pais de origem da banda em ISO 3166-1 alpha-2 (ex: BR)';
