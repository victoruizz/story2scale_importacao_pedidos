CREATE TABLE importacao (
    id BIGSERIAL PRIMARY KEY,
    nome_arquivo VARCHAR(255),
    status VARCHAR(30),
    total_linhas INTEGER,
    linhas_validas INTEGER,
    linhas_invalidas INTEGER,
    criado_em TIMESTAMP
);

CREATE TABLE pedido (
    id BIGSERIAL PRIMARY KEY,
    numero_pedido VARCHAR(255) NOT NULL UNIQUE,
    cliente VARCHAR(255),
    documento_cliente VARCHAR(255),
    produto VARCHAR(255),
    quantidade INTEGER,
    valor_unitario NUMERIC(15, 2),
    valor_total NUMERIC(15, 2),
    data_pedido DATE,
    criado_em TIMESTAMP,
    importacao_id BIGINT REFERENCES importacao(id)
);

CREATE TABLE erro_importacao (
    id BIGSERIAL PRIMARY KEY,
    importacao_id BIGINT REFERENCES importacao(id),
    linha INTEGER,
    numero_pedido VARCHAR(255),
    campo VARCHAR(255),
    mensagem VARCHAR(255),
    valor_recebido VARCHAR(255)
);