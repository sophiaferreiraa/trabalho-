<?php
session_start();

class FinalizarCompra {

    public function index() {

        if (!isset($_SESSION["cliente"]) || empty($_SESSION["cliente"])) {
            header("Location: /e-commerce/public/index.php?url=login&erro=nao_autenticado");
            exit;
        }

        if (!isset($_SESSION["carrinho"]) || count($_SESSION["carrinho"]) == 0) {
            echo "<h2>Seu carrinho está vazio.</h2>";
            exit;
        }

        $cliente = $_SESSION["cliente"];
        $carrinho = $_SESSION["carrinho"];

        $total = 0;
        foreach ($carrinho as $item) {
            $total += $item["preco"] * $item["quantidade"];
        }

        require_once __DIR__ . "/../database/Conexao.php";
        $db = Conexao::getInstance();

        $sql = "INSERT INTO pedidos (id_cliente, total, data_pedido) VALUES (:id_cliente, :total, NOW())";
        $stmt = $db->prepare($sql);
        $stmt->bindValue(":id_cliente", $cliente["id"]);
        $stmt->bindValue(":total", $total);
        $stmt->execute();

        $id_pedido = $db->lastInsertId();

        $sql2 = "INSERT INTO pedido_itens (id_pedido, id_produto, quantidade, preco) 
                 VALUES (:id_pedido, :id_produto, :quantidade, :preco)";
        $stmt2 = $db->prepare($sql2);

        foreach ($carrinho as $item) {
            $stmt2->execute([
                ":id_pedido" => $id_pedido,
                ":id_produto" => $item["id"],
                ":quantidade" => $item["quantidade"],
                ":preco" => $item["preco"]
            ]);
        }

        unset($_SESSION["carrinho"]);

        header("Location: /e-commerce/public/index.php?url=pedido_sucesso&id=".$id_pedido);
        exit;
    }
}

$controller = new FinalizarCompra();
$controller->index();
