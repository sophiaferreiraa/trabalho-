-- 1. Criação do Banco de Dados (conforme string em Conexao.java)
CREATE DATABASE IF NOT EXISTS cadastro_pessoa;
USE cadastro_pessoa;
-- 2. Criação da Tabela (conforme queries em PessoaDAO.java)
CREATE TABLE IF NOT EXISTS pessoa (
    id INTEGER PRIMARY KEY,  -- para fazer id alto incremente : id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL, -- String nome em Pessoa.java
    escola VARCHAR(255) NOT NULL -- String email em Pessoa.java
);