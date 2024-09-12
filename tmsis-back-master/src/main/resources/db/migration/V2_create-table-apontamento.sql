CREATE TABLE apontamento (
    id SERIAL PRIMARY KEY,
    usuario VARCHAR(255) NOT NULL,
    data_apontamento DATE NOT NULL,
    horario_apontamento TIME NOT NULL,
    tipo VARCHAR(10)
);
