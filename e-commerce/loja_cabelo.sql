CREATE DATABASE IF NOT EXISTS loja_cabelo;
USE loja_cabelo;

CREATE TABLE IF NOT EXISTS Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL, 
    data_nascimento DATE,
    tipo_usuario VARCHAR(50) DEFAULT 'usuario'
);

CREATE TABLE IF NOT EXISTS Brands (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS Hair_Types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS Treatment_Types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tratamento VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS Curl_Patterns (
    id INT AUTO_INCREMENT PRIMARY KEY,
    curvatura VARCHAR(10) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS Product_Types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL
);

-- TABELA PRINCIPAL DE PRODUTOS
CREATE TABLE IF NOT EXISTS Products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao_curta VARCHAR(255),
    preco DECIMAL(10, 2) NOT NULL,
    estoque INT DEFAULT 0,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    brand_id INT,
    FOREIGN KEY (brand_id) REFERENCES Brands(id) ON DELETE RESTRICT
);

-- TABELAS DE RELACIONAMENTO E VENDAS

CREATE TABLE IF NOT EXISTS Product_Hair_Types (
    product_id INT NOT NULL,
    hair_type_id INT NOT NULL,
    PRIMARY KEY (product_id, hair_type_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (hair_type_id) REFERENCES Hair_Types(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Product_Treatments (
    product_id INT NOT NULL,
    treatment_id INT NOT NULL,
    PRIMARY KEY (product_id, treatment_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (treatment_id) REFERENCES Treatment_Types(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Product_Curl_Patterns (
    product_id INT NOT NULL,
    curl_pattern_id INT NOT NULL,
    PRIMARY KEY (product_id, curl_pattern_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (curl_pattern_id) REFERENCES Curl_Patterns(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Product_Product_Types (
    product_id INT NOT NULL,
    product_type_id INT NOT NULL,
    PRIMARY KEY (product_id, product_type_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (product_type_id) REFERENCES Product_Types(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Carts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_cart (user_id)
);

CREATE TABLE IF NOT EXISTS Cart_Items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantidade INT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES Carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS Order_Items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    nome_produto VARCHAR(150) NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    quantidade INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE RESTRICT
);

--  INSERÇÃO DE DADOS 

INSERT IGNORE INTO Hair_Types (tipo) VALUES 
('Liso'), ('Ondulado'), ('Cacheado'), ('Crespo');

INSERT IGNORE INTO Treatment_Types (tratamento) VALUES 
('Hidratação'), ('Nutrição'), ('Reconstrução'), ('Reparação'), ('Pós Química'), 
('Pre Poo'), ('Transição Capilar'), ('Acidificação'), ('Matização'), ('Umectação');

INSERT IGNORE INTO Curl_Patterns (curvatura) VALUES 
('1'), ('2A'), ('2B'), ('2C'), 
('3A'), ('3B'), ('3C'), 
('4A'), ('4B'), ('4C');

INSERT IGNORE INTO Product_Types (nome) VALUES 
('Shampoo'), ('Condicionador'), ('Máscara'), ('Óleo de Cabelo'), 
('Finalizador'), ('Kit/Conjunto'), ('Leave-in'), ('Tônico Capilar');

INSERT IGNORE INTO Brands (id, nome) VALUES 
(1, 'Wella Professionals'), 
(2, 'L''Oréal Professionnel'), 
(3, 'Kérastase'),
(4, 'Redken'), 
(5, 'Cadiveu Professional'),
(6, 'Truss Professional'),
(7, 'Amend'),
(8, 'Bio Extratus'),
(9, 'Salon Line'),
(10, 'Skala');

INSERT INTO Products (id, nome, descricao_curta, preco, estoque, brand_id) VALUES
(1, 'Shampoo Hidratante Wella', 'Shampoo para brilho intenso e maciez.', 89.90, 100, 1),
(2, 'Máscara Nutritiva LOréal', 'Máscara para nutrição profunda e reparação.', 149.90, 100, 2)
ON DUPLICATE KEY UPDATE estoque = 100;