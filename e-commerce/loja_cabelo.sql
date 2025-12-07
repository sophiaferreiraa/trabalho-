-- create database loja_cabelo;
-- use loja_cabelo;

-- 1. ESTRUTURA DE USUÁRIOS E AUTENTICAÇÃO

CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL, -- Senha HASHED (password_hash)
    data_nascimento DATE,
    tipo_usuario VARCHAR(50) DEFAULT 'usuario' -- 'usuario' ou 'admin'
);

-- 2. ESTRUTURA DE PRODUTOS E FILTRAGEM

-- Tabela Principal de Produtos
CREATE TABLE Products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao_curta VARCHAR(255),
    preco DECIMAL(10, 2) NOT NULL,
    estoque INT DEFAULT 0,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabelas de Categorias de Filtro (Lookups)
CREATE TABLE Hair_Types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(100) UNIQUE NOT NULL -- Ex: Liso, Ondulado, Cacheado, Crespo
);

CREATE TABLE Treatment_Types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tratamento VARCHAR(100) UNIQUE NOT NULL -- Ex: Nutrição, Reconstrução, Pós Química
);

CREATE TABLE Curl_Patterns (
    id INT AUTO_INCREMENT PRIMARY KEY,
    curvatura VARCHAR(10) UNIQUE NOT NULL -- Ex: 1, 2A, 3C, 4B
);
CREATE TABLE Product_Types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL
);
CREATE TABLE Brands (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL
);

-- Tabelas de Relacionamento N:N (Filtragem Eficiente)
CREATE TABLE Product_Hair_Types (
    product_id INT NOT NULL,
    hair_type_id INT NOT NULL,
    PRIMARY KEY (product_id, hair_type_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (hair_type_id) REFERENCES Hair_Types(id) ON DELETE CASCADE
);

CREATE TABLE Product_Treatments (
    product_id INT NOT NULL,
    treatment_id INT NOT NULL,
    PRIMARY KEY (product_id, treatment_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (treatment_id) REFERENCES Treatment_Types(id) ON DELETE CASCADE
);

CREATE TABLE Product_Curl_Patterns (
    product_id INT NOT NULL,
    curl_pattern_id INT NOT NULL,
    PRIMARY KEY (product_id, curl_pattern_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (curl_pattern_id) REFERENCES Curl_Patterns(id) ON DELETE CASCADE
);

CREATE TABLE Product_Product_Types (
    product_id INT NOT NULL,
    product_type_id INT NOT NULL,
    PRIMARY KEY (product_id, product_type_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (product_type_id) REFERENCES Product_Types(id) ON DELETE CASCADE
);

-- 3. ESTRUTURA DE VENDAS E HISTÓRICO (CARRINHO E PEDIDOS)

-- Carrinho de Compras Ativo
CREATE TABLE Carts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_cart (user_id)
);

-- Itens dentro do Carrinho
CREATE TABLE Cart_Items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantidade INT NOT NULL,

    FOREIGN KEY (cart_id) REFERENCES Carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);

-- Histórico de Pedidos Finalizados (Minhas Compras)
CREATE TABLE Orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Usado para o filtro de período do admin
    valor_total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL, -- Ex: 'Confirmado', 'Enviado', 'Entregue'
    
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE RESTRICT
);

-- Itens do Pedido (Cópia estática dos dados no momento da compra)
CREATE TABLE Order_Items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    nome_produto VARCHAR(150) NOT NULL, -- Cópia estática
    preco_unitario DECIMAL(10, 2) NOT NULL, -- Cópia estática
    quantidade INT NOT NULL,
    
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE RESTRICT
);
ALTER TABLE Products
ADD COLUMN brand_id INT,
ADD FOREIGN KEY (brand_id) REFERENCES Brands(id) ON DELETE RESTRICT;

-- Inserindo Tipos de Cabelo
INSERT INTO Hair_Types (tipo) VALUES 
('Liso'), ('Ondulado'), ('Cacheado'), ('Crespo');

-- Inserindo Tipos de Tratamento
INSERT INTO Treatment_Types (tratamento) VALUES 
('Hidratação'), ('Nutrição'), ('Reconstrução'), ('Reparação'), ('Pós Química'), 
('Pre Poo'), ('Transição Capilar'), ('Acidificação'), ('Matização'), ('Umectação');

-- Inserindo Curvaturas
INSERT INTO Curl_Patterns (curvatura) VALUES 
('1'), ('2A'), ('2B'), ('2C'), 
('3A'), ('3B'), ('3C'), 
('4A'), ('4B'), ('4C');

-- Inserindo tipos de produtos
INSERT INTO Product_Types (nome) VALUES 
('Shampoo'), 
('Condicionador'), 
('Máscara'),
('Óleo de Cabelo'), 
('Finalizador'),
('Kit/Conjunto'),
('Leave-in'),
('Tônico Capilar');

-- Inserindo marcas 
INSERT INTO Brands (nome) VALUES 
('Wella Professionals'), 
('L''Oréal Professionnel'), 
('Kérastase'),
('Redken'), 
('Cadiveu Professional'),
('Truss Professional'),
('Amend'),
('Bio Extratus'),
('Salon Line'),
('Skala');