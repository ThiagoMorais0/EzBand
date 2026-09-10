-- Ano de fundacao da banda e natureza do repertorio (autoral / cover / mix).
-- Ambos opcionais: quando nulos, o perfil da banda simplesmente omite a informacao.
-- ano_fundacao nao substitui data_inclusao, que e a data de entrada da banda no EzBand.

ALTER TABLE banda
ADD COLUMN IF NOT EXISTS ano_fundacao INTEGER;

ALTER TABLE banda
ADD COLUMN IF NOT EXISTS tipo_repertorio VARCHAR(20);

COMMENT ON COLUMN banda.ano_fundacao IS 'Ano em que a banda foi fundada (opcional)';
COMMENT ON COLUMN banda.tipo_repertorio IS 'Natureza do repertorio: AUTORAL, COVER ou MIX (opcional)';
