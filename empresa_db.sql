-- Arquivo: empresa_db.sql

-- 1. Cria o banco de dados
CREATE DATABASE empresa_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Portuguese_Brazil.1252'
    LC_CTYPE = 'Portuguese_Brazil.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- 2. Cria a tabela TipoProduto
CREATE TABLE tipo_produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

-- 3. Cria a tabela Produto com Chave Estrangeira (FK)
CREATE TABLE produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    descricao TEXT,
    id_tipo_produto INTEGER NOT NULL,
    CONSTRAINT fk_tipo_produto
        FOREIGN KEY (id_tipo_produto)
        REFERENCES tipo_produto (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- 4. Insere dados de teste para TipoProduto
INSERT INTO tipo_produto (nome) VALUES ('Vinho Tinto');
INSERT INTO tipo_produto (nome) VALUES ('Vinho Branco');
INSERT INTO tipo_produto (nome) VALUES ('Espumante');
INSERT INTO tipo_produto (nome) VALUES ('Cerveja Artesanal');

-- 5. Insere dados de teste para Produto
INSERT INTO produto (nome, preco, descricao, id_tipo_produto) VALUES
('Cabernet Sauvignon Reserva', 95.50, 'Vinho tinto seco de corpo médio.', 1);

INSERT INTO produto (nome, preco, descricao, id_tipo_produto) VALUES
('Chardonnay Leve', 45.00, 'Vinho branco frutado e refrescante.', 2);