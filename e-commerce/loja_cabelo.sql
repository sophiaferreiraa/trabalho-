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

INSERT INTO Products (nome, descricao_curta, preco, estoque, brand_id) VALUES
('Wella Invigo Nutri-Enrich Shampoo 300ml',
 'Shampoo nutritivo para cabelos secos e ressecados.',
 89.90, 100, 1),

('Wella Oil Reflections Óleo Capilar 100ml',
 'Óleo iluminador que proporciona brilho instantâneo.',
 119.90, 100, 1),

('L’Oréal Professionnel Absolut Repair Shampoo 300ml',
 'Shampoo reconstrutor para fios danificados.',
 129.90, 100, 2),

('L’Oréal Professionnel Absolut Repair Máscara 250g',
 'Máscara nutritiva e restauradora para fios quebradiços.',
 169.90, 100, 2),

('Kérastase Discipline Curl Idéal Creme 150ml',
 'Finalizador para cabelos cacheados e ondulados.',
 199.90, 100, 3),

('Kérastase Elixir Ultime Óleo Original 100ml',
 'Óleo finalizador para brilho e controle do frizz.',
 219.90, 100, 3),

('Redken All Soft Shampoo 300ml',
 'Shampoo hidratante com óleo de argan para maciez.',
 124.90, 100, 4),

('Redken Extreme Condicionador 300ml',
 'Condicionador fortalecedor para reconstrução capilar.',
 129.90, 100, 4),

('Cadiveu Nutri Glow Máscara 500g',
 'Máscara nutritiva para brilho e sedosidade.',
 79.90, 100, 5),

('Cadiveu Plástica dos Fios Shampoo 300ml',
 'Shampoo antifrizz para cabelos lisos e com química.',
 69.90, 100, 5),

('Truss Ultra Hydration Shampoo 300ml',
 'Shampoo de hidratação intensa para fios opacos.',
 98.90, 100, 6),

('Truss Uso Obrigatório Reconstrutor 260ml',
 'Reconstrutor capilar para danos profundos.',
 149.90, 100, 6),

('Amend Millenar Óleo de Coco Máscara 300g',
 'Máscara nutritiva com coco para cabelos ressecados.',
 47.90, 100, 7),

('Salon Line #ToDeCacho Creme 3ABC 1kg',
 'Creme para pentear para curvaturas 3A a 3C.',
 24.90, 100, 9),

('Skala Mais Cachos Creme 1kg',
 'Creme multifuncional vegano para todos os cachos.',
 12.90, 100, 10);

INSERT INTO Product_Product_Types (product_id, product_type_id) VALUES
(1, 1),
(2, 4),
(3, 1),
(4, 3),
(5, 5),
(6, 4),
(7, 1),
(8, 2),
(9, 3),
(10, 1),
(11, 1),
(12, 7),
(13, 3),
(14, 5),
(15, 5);

INSERT INTO Product_Treatments (product_id, treatment_id) VALUES
-- Produto 1
(1, 1), (1, 2),

-- Produto 2
(2, 2), (2, 4),

-- Produto 3
(3, 3),

-- Produto 4
(4, 3), (4, 2),

-- Produto 5
(5, 2),

-- Produto 6
(6, 2),

-- Produto 7
(7, 1),

-- Produto 8
(8, 3),

-- Produto 9
(9, 2),

-- Produto 10
(10, 5), (10, 3),

-- Produto 11
(11, 1),

-- Produto 12
(12, 3), (12, 4),

-- Produto 13
(13, 2),

-- Produto 14
(14, 2), (14, 10),

-- Produto 15
(15, 1);

INSERT INTO Product_Hair_Types (product_id, hair_type_id) VALUES
-- Todos os cabelos
(1,1),(1,2),(1,3),(1,4),
(2,1),(2,2),(2,3),(2,4),
(3,1),(3,2),(3,3),(3,4),
(4,1),(4,2),(4,3),(4,4),
(6,1),(6,2),(6,3),(6,4),
(7,1),(7,2),(7,3),(7,4),
(8,1),(8,2),(8,3),(8,4),
(9,1),(9,2),(9,3),(9,4),
(11,1),(11,2),(11,3),(11,4),
(12,1),(12,2),(12,3),(12,4),

-- Ondulados/Cacheados
(5,2),(5,3),

-- Ressecados/cacheados especificamente
(13,2),(13,3),(13,4),

-- Produtos para cachos
(14,3),
(15,3),(15,4),

-- Lisos e alisados
(10,1),(10,2);
INSERT INTO Product_Curl_Patterns (product_id, curl_pattern_id) VALUES
-- Produtos para todos os tipos
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),
(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),(2,10),
(3,1),(3,2),(3,3),(3,4),(3,5),(3,6),(3,7),(3,8),(3,9),(3,10),
(4,1),(4,2),(4,3),(4,4),(4,5),(4,6),(4,7),(4,8),(4,9),(4,10),
(6,2),(6,3),(6,4),(6,5),(6,6),(6,7),(6,8),(6,9),(6,10),
(7,1),(7,2),(7,3),(7,4),(7,5),(7,6),(7,7),(7,8),(7,9),(7,10),
(8,1),(8,2),(8,3),(8,4),(8,5),(8,6),(8,7),(8,8),(8,9),(8,10),
(9,1),(9,2),(9,3),(9,4),(9,5),(9,6),(9,7),(9,8),(9,9),(9,10),
(11,1),(11,2),(11,3),(11,4),(11,5),(11,6),(11,7),(11,8),(11,9),(11,10),
(12,1),(12,2),(12,3),(12,4),(12,5),(12,6),(12,7),(12,8),(12,9),(12,10),

-- Produto 5 – Curl Idéal (2B–3C)
(5,3),(5,4),(5,5),(5,6),(5,7),

-- Produto 10 – Lisos e 2A/B
(10,1),(10,2),(10,3),

-- Produto 13 – Coco (2A–4C)
(13,2),(13,3),(13,4),(13,5),(13,6),(13,7),(13,8),(13,9),(13,10),

-- Produto 14 – Linha 3ABC
(14,5),(14,6),(14,7),

-- Produto 15 – Cachos e crespos 3A–4C
(15,5),(15,6),(15,7),(15,8),(15,9),(15,10);
