-- Tabela REPORTE_DE_ERRO
CREATE TABLE IF NOT EXISTS reporte_de_erro (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT,
    mensagem TEXT,
    CONSTRAINT fk_reporte_erro_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
);

CREATE INDEX idx_reporte_erro_usuario ON reporte_de_erro(id_usuario);
