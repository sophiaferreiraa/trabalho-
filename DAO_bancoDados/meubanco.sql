-- Cria o banco de dados
CREATE DATABASE IF NOT EXISTS meubanco;

-- Usa o banco criado
USE meubanco;

-- Cria a tabela de contatos
CREATE TABLE IF NOT EXISTS contatos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);
