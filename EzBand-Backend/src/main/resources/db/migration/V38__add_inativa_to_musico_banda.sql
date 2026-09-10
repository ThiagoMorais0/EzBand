-- Inativacao de banda por musico (nao pela banda).
-- Cada membro decide se a banda aparece no seu proprio painel; a banda continua
-- ativa para os demais membros.

ALTER TABLE musico_banda
ADD COLUMN IF NOT EXISTS inativa BOOLEAN DEFAULT FALSE;

UPDATE musico_banda SET inativa = FALSE WHERE inativa IS NULL;

COMMENT ON COLUMN musico_banda.inativa IS 'Banda oculta no painel deste musico (preferencia individual, nao afeta os outros membros)';
