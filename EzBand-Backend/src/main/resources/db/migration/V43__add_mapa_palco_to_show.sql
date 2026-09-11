-- Vincula um mapa de palco a um show especifico. Nulo significa "usar o mapa
-- padrao da banda" -- a maioria dos shows nunca precisa escolher.
-- ON DELETE SET NULL: excluir um mapa nao pode apagar o show.
ALTER TABLE show
    ADD COLUMN IF NOT EXISTS id_mapa_palco BIGINT;

ALTER TABLE show
    DROP CONSTRAINT IF EXISTS fk_show_mapa_palco;

ALTER TABLE show
    ADD CONSTRAINT fk_show_mapa_palco
    FOREIGN KEY (id_mapa_palco) REFERENCES mapa_palco(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_show_mapa_palco ON show(id_mapa_palco);
