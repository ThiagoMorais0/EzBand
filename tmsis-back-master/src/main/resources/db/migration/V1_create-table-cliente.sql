CREATE TABLE cliente (
    cpfcnpj BIGINT PRIMARY KEY,
    nome VARCHAR(255),
    apelido VARCHAR(255),
    tipo INTEGER,
    telefone BIGINT,
    email VARCHAR(255),
    identificacao_fiscal BIGINT,
    end_cep BIGINT,
    end_logradouro VARCHAR(255),
    end_cidade VARCHAR(255),
    end_bairro VARCHAR(255),
    end_numero VARCHAR(50),
    end_complemento VARCHAR(255),
    end_referencia VARCHAR(255),
    end_observacao TEXT,
    data_inclusao TIMESTAMP
);
