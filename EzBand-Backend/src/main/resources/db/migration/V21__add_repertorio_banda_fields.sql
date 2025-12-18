-- Adicionar novos campos na tabela REPERTORIO_BANDA

-- Adicionar campo índice para ordenação
ALTER TABLE repertorio_banda
ADD COLUMN indice INTEGER;

-- Adicionar campo posição no show (1-10)
ALTER TABLE repertorio_banda
ADD COLUMN posicao_show INTEGER CHECK (posicao_show >= 1 AND posicao_show <= 10);

-- Adicionar campo energia (1-10)
ALTER TABLE repertorio_banda
ADD COLUMN energia INTEGER CHECK (energia >= 1 AND energia <= 10);

-- Adicionar campo relevância (1-10)
ALTER TABLE repertorio_banda
ADD COLUMN relevancia INTEGER CHECK (relevancia >= 1 AND relevancia <= 10);

-- Criar índice para ordenação por índice
CREATE INDEX IF NOT EXISTS idx_repertorio_banda_indice
    ON repertorio_banda(id_banda, indice);

-- Comentários nas colunas
COMMENT ON COLUMN repertorio_banda.indice IS 'Ordem da música no setlist da banda';
COMMENT ON COLUMN repertorio_banda.posicao_show IS 'Posição ideal no show: 1-3 (início), 4-7 (meio), 8-10 (fim)';
COMMENT ON COLUMN repertorio_banda.energia IS 'Nível de energia da música: 1-3 (calma), 4-7 (moderada), 8-10 (agitada)';
COMMENT ON COLUMN repertorio_banda.relevancia IS 'Relevância da música: 1-3 (baixa), 4-7 (média), 8-10 (alta)';
