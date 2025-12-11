<?php
require_once 'ProdutoDAO.php';



class CarrinhoDAO {

    public static function registrarVenda($id_usuario, $itens) {
        $pdo = Database::conectar();
        $pdo->beginTransaction();

        try {
            // cria venda
            $stmt = $pdo->prepare("INSERT INTO venda (id_usuario, data_venda) VALUES (?, NOW())");
            $stmt->execute([$id_usuario]);
            $id_venda = $pdo->lastInsertId();

            // insere itens da venda
            foreach ($itens as $i) {
                $stmtItem = $pdo->prepare("
                    INSERT INTO venda_item (id_venda, id_produto, quantidade, subtotal)
                    VALUES (?, ?, ?, ?)
                ");
                $stmtItem->execute([$id_venda, $i->id_produto, $i->qtd, $i->subtotal]);
            }

            $pdo->commit();
            return true;

        } catch (Exception $e) {
            $pdo->rollback();
            return false;
        }
    }
}
