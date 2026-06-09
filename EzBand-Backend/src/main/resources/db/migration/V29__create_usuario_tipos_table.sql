CREATE TABLE usuario_tipos (
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    CONSTRAINT fk_usuario_tipos_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
