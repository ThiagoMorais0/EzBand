-- Codigo curto digitavel do convite externo, alternativa ao token do link.
-- Existe porque o link de convite sempre abre no navegador: quem ja tem o EzBand
-- instalado como PWA nao pode ser redirecionado do navegador para o app, entao
-- digita o codigo dentro do app. Nulo nos convites gerados antes desta coluna --
-- esses continuam validos apenas pelo link.
ALTER TABLE convite_externo_banda
    ADD COLUMN IF NOT EXISTS codigo VARCHAR(16);

-- UNIQUE permite varios NULL no Postgres, o que cobre os convites antigos.
CREATE UNIQUE INDEX IF NOT EXISTS idx_convite_externo_codigo
    ON convite_externo_banda(codigo);
