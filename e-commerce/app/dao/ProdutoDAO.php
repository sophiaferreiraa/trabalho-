<?php
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../models/ProdutoModel.php';

class ProdutoDAO {

    private $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

    public function listarHairTypes() {
        return $this->pdo->query("SELECT * FROM hair_types ORDER BY tipo ASC")
                         ->fetchAll(PDO::FETCH_ASSOC);
    }

    public function listarCurlPatterns() {
        return $this->pdo->query("SELECT * FROM curl_patterns ORDER BY curvatura ASC")
                         ->fetchAll(PDO::FETCH_ASSOC);
    }

    public function listarProductTypes() {
        return $this->pdo->query("SELECT * FROM product_types ORDER BY nome ASC")
                         ->fetchAll(PDO::FETCH_ASSOC);
    }

    public function listarTreatments() {
        return $this->pdo->query("SELECT * FROM treatment_types ORDER BY tratamento ASC")
                         ->fetchAll(PDO::FETCH_ASSOC);
    }

    public function listarBrands() {
        return $this->pdo->query("SELECT * FROM brands ORDER BY nome ASC")
                         ->fetchAll(PDO::FETCH_ASSOC);
    }

    public function listarCategorias() {
        return $this->pdo->query("SELECT * FROM Product_Types ORDER BY nome ASC")
                         ->fetchAll(PDO::FETCH_ASSOC);
    }

    public static function buscarPorId($id) {
        $pdo = Database::getConnection();
        $sql = "SELECT * FROM products WHERE id = ?";
        $stmt = $pdo->prepare($sql);
        $stmt->execute([$id]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    public function filtrarProdutos($hairType, $curvatura, $type, $treatment, $brand, $preco) {
        $sql = "SELECT DISTINCT 
                p.*, 
                b.nome AS brand_name,
                ht.id AS hair_type_id,
                ht.tipo AS hair_type,
                cp.id AS curl_pattern_id,
                cp.curvatura AS curl_pattern,
                pt.id AS product_type_id,
                pt.nome AS product_type,
                tt.id AS treatment_type_id,
                tt.tratamento AS treatment_type
            FROM products p
            LEFT JOIN brands b ON p.brand_id = b.id
            LEFT JOIN product_hair_types pht ON p.id = pht.product_id
            LEFT JOIN hair_types ht ON pht.hair_type_id = ht.id
            LEFT JOIN product_curl_patterns pcp ON p.id = pcp.product_id
            LEFT JOIN curl_patterns cp ON pcp.curl_pattern_id = cp.id
            LEFT JOIN product_product_types ppt ON p.id = ppt.product_id
            LEFT JOIN product_types pt ON ppt.product_type_id = pt.id
            LEFT JOIN product_treatments ptr ON p.id = ptr.product_id
            LEFT JOIN treatment_types tt ON ptr.treatment_id = tt.id
            WHERE 1=1 ";

        $params = [];

        if ($hairType) {
            $sql .= " AND ht.id = :hairType ";
            $params['hairType'] = $hairType;
        }

        if ($curvatura) {
            $sql .= " AND cp.id = :curvatura ";
            $params['curvatura'] = $curvatura;
        }

        if ($type) {
            $sql .= " AND pt.id = :type ";
            $params['type'] = $type;
        }

        if ($treatment) {
            $sql .= " AND tt.id = :treatment ";
            $params['treatment'] = $treatment;
        }

        if ($brand) {
            $sql .= " AND b.id = :brand ";
            $params['brand'] = $brand;
        }

        if ($preco) {
            if ($preco == 1) $sql .= " AND p.preco <= 30 ";
            if ($preco == 2) $sql .= " AND p.preco BETWEEN 30 AND 70 ";
            if ($preco == 3) $sql .= " AND p.preco BETWEEN 70 AND 150 ";
            if ($preco == 4) $sql .= " AND p.preco > 150 ";
        }

        $sql .= " GROUP BY p.id ORDER BY p.id DESC ";

        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);

        return $this->mapearLista($stmt->fetchAll(PDO::FETCH_ASSOC));
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

    private function mapearLista($rows) {
        $lista = [];
        foreach ($rows as $row) {
            $p = new ProdutoModel();
            $p->setId($row['id']);
            $p->setNome($row['nome']);
            $p->setDescricao($row['descricao_curta']);
            $p->setPreco($row['preco']);
            $p->setEstoque($row['estoque']);

            $p->setBrandId($row['brand_id'] ?? null);
            $p->setBrandName($row['brand_name'] ?? null);
            $p->setHairTypeId($row['hair_type_id'] ?? null);
            $p->setHairType($row['hair_type'] ?? null);
            $p->setCurlPatternId($row['curl_pattern_id'] ?? null);
            $p->setCurlPattern($row['curl_pattern'] ?? null);
            $p->setProductTypeId($row['product_type_id'] ?? null);
            $p->setProductType($row['product_type'] ?? null);
            $p->setTreatmentTypeId($row['treatment_type_id'] ?? null);
            $p->setTreatmentType($row['treatment_type'] ?? null);

            $p->setCategoriaId($row['categoria_id'] ?? null);
            $p->setNomeCategoria($row['nome_categoria'] ?? 'Sem Categoria');

            $lista[] = $p;
        }
        return $lista;
    }
}
