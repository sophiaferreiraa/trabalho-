<?php
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../models/ProdutoModel.php';

class ProdutoDAO {
    private $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

    public function listarTodos() {
        $sql = "SELECT p.*, pt.nome as nome_categoria, pt.id as categoria_id 
                FROM Products p
                LEFT JOIN Product_Product_Types ppt ON p.id = ppt.product_id
                LEFT JOIN Product_Types pt ON ppt.product_type_id = pt.id
                ORDER BY p.id DESC";
        
        $stmt = $this->pdo->query($sql);
        return $this->mapearLista($stmt->fetchAll(PDO::FETCH_ASSOC));
    }

    public function listarPorCategoria($categoriaId) {
        $sql = "SELECT p.*, pt.nome as nome_categoria, pt.id as categoria_id 
                FROM Products p
                JOIN Product_Product_Types ppt ON p.id = ppt.product_id
                JOIN Product_Types pt ON ppt.product_type_id = pt.id
                WHERE pt.id = ?
                ORDER BY p.nome ASC";

        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([$categoriaId]);
        return $this->mapearLista($stmt->fetchAll(PDO::FETCH_ASSOC));
    }

    public function cadastrar(ProdutoModel $produto) {
        try {
            $this->pdo->beginTransaction();

            $sql = "INSERT INTO Products (nome, descricao_curta, preco, estoque) VALUES (?, ?, ?, ?)";
            $stmt = $this->pdo->prepare($sql);
            $stmt->execute([
                $produto->getNome(),
                $produto->getDescricao(),
                $produto->getPreco(),
                $produto->getEstoque()
            ]);
            
            $idProduto = $this->pdo->lastInsertId();

            if ($produto->getCategoriaId()) {
                $sqlRel = "INSERT INTO Product_Product_Types (product_id, product_type_id) VALUES (?, ?)";
                $stmtRel = $this->pdo->prepare($sqlRel);
                $stmtRel->execute([$idProduto, $produto->getCategoriaId()]);
            }

            $this->pdo->commit();
            return true;
        } catch (Exception $e) {
            $this->pdo->rollBack();
            return false;
        }
    }

    public function excluir($id) {
        $sql = "DELETE FROM Products WHERE id = ?";
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute([$id]);
    }

    public function listarCategorias() {
        $stmt = $this->pdo->query("SELECT * FROM Product_Types ORDER BY nome ASC");
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    private function mapearLista($rows) {
        $lista = [];
        foreach ($rows as $row) {
            $p = new ProdutoModel();
            $p->setId($row['id']);
            $p->setNome($row['nome']);
            $p->setDescricao($row['descricao_curta']);
            $p->setPreco($row['preco']);
            $p->setEstoque($row['estoque']);
            $p->setCategoriaId($row['categoria_id'] ?? null);
            $p->setNomeCategoria($row['nome_categoria'] ?? 'Geral');
            $lista[] = $p;
        }
        return $lista;
    }
}