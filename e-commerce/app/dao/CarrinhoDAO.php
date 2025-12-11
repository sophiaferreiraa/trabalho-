<?php
require_once 'ProdutoDAO.php';

class CarrinhoDAO {

    public static function registrarVenda($id_usuario, $itens) {

        $db = database::getConnection();

        try {

            $db->beginTransaction();

            $total = 0;
            foreach ($itens as $i) {
                $total += $i->getSubtotal();
            }

            $stmt = $db->prepare("
                INSERT INTO Orders (user_id, valor_total, status)
                VALUES (?, ?, 'Pendente')
            ");
            $stmt->execute([$id_usuario, $total]);

            $orderId = $db->lastInsertId();

            foreach ($itens as $i) {

                $stmtItem = $db->prepare("
                    INSERT INTO Order_Items 
                    (order_id, product_id, nome_produto, preco_unitario, quantidade)
                    VALUES (?, ?, ?, ?, ?)
                ");

                $stmtItem->execute([
                    $orderId,
                    $i->getId(),
                    "",
                    $i->getPreco(),
                    $i->getQtd()
                ]);
            }

            $db->commit();
            return true;

        } catch (Exception $e) {
            $db->rollBack();
            return false;
        }
    }
}
