<?php
require_once __DIR__ . '/../../config/database.php';

class Venda {
    private $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

    // Listar compras de um usuário específico
    public function listarPorCliente($userId) {
        $sql = "SELECT * FROM Orders WHERE user_id = ? ORDER BY data_pedido DESC";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([$userId]);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    // Listar todas as vendas (para o Admin), com filtro opcional de data
    public function listarTodasVendas($dataInicio = null, $dataFim = null) {
        $sql = "SELECT o.id, o.data_pedido, o.valor_total, o.status, u.nome_completo 
                FROM Orders o
                JOIN Users u ON o.user_id = u.id";
        
        $params = [];
        
        if ($dataInicio && $dataFim) {
            $sql .= " WHERE DATE(o.data_pedido) BETWEEN ? AND ?";
            $params[] = $dataInicio;
            $params[] = $dataFim;
        }

        $sql .= " ORDER BY o.data_pedido DESC";

        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    // Registrar uma nova venda (Simulação)
    public function registrarVenda($userId, $total, $itens) {
        try {
            $this->pdo->beginTransaction();

            // 1. Cria o Pedido
            $sqlOrder = "INSERT INTO Orders (user_id, valor_total, status) VALUES (?, ?, 'Confirmado')";
            $stmt = $this->pdo->prepare($sqlOrder);
            $stmt->execute([$userId, $total]);
            $orderId = $this->pdo->lastInsertId();

            // 2. Insere os Itens
            $sqlItem = "INSERT INTO Order_Items (order_id, product_id, nome_produto, preco_unitario, quantidade) VALUES (?, ?, ?, ?, ?)";
            $stmtItem = $this->pdo->prepare($sqlItem);

            foreach ($itens as $item) {
                $stmtItem->execute([
                    $orderId, 
                    $item['id'], 
                    $item['nome'], 
                    $item['preco'], 
                    $item['quantidade']
                ]);
            }

            $this->pdo->commit();
            return true;
        } catch (Exception $e) {
            $this->pdo->rollBack();
            return false;
        }
    }
}