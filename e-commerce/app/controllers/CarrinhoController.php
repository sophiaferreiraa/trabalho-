<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

require_once __DIR__ . '/../models/ItemCarrinho.php';
require_once __DIR__ . '/../models/CarrinhoModel.php';
require_once __DIR__ . '/../dao/CarrinhoDAO.php';
require_once __DIR__ . '/../dao/ProdutoDAO.php';

class CarrinhoController {

    public function paginaCompra() {
        $produtos = ProdutoDAO::listarTodos();
        require __DIR__ . '/../views/cliente/paginaCompra.php';
    }

    public function adicionar() {
        $id = intval($_GET['id'] ?? 0);
        $produto = ProdutoDAO::buscarPorId($id);

        if (!$produto) {
            header("Location: /e-commerce/public/index.php?url=produtos");
            exit;
        }

        $item = new ItemCarrinho($id, 1, $produto['preco']);
        CarrinhoModel::adicionarItem($item);

        header("Location: /e-commerce/public/index.php?url=carrinho");
        exit;
    }

    public function alterar() {
        $id = $_POST['id_produto'];
        $qtd = intval($_POST['qtd']);
        CarrinhoModel::alterarQuantidade($id, $qtd);

        header("Location: /e-commerce/public/index.php?url=carrinho");
        exit;
    }

    public function remover() {
        $id = $_GET['id_produto'];
        CarrinhoModel::removerItem($id);

        header("Location: /e-commerce/public/index.php?url=carrinho");
        exit;
    }

    public function carrinho() {
        $carrinho = CarrinhoModel::getCarrinho();
        require __DIR__ . '/../views/cliente/carrinho.php';
    }

    public function finalizar() {
    $id_usuario = $_SESSION['user_id'] ?? null;

    if (!$id_usuario) {
        echo "Usuário não autenticado.";
        exit;
    }

    $itens = CarrinhoModel::getCarrinho();
    $ok = CarrinhoDAO::registrarVenda($id_usuario, $itens);

    if ($ok) {
        CarrinhoModel::limpar();
        header("Location: /e-commerce/public/index.php?url=minhas_compras");
    } else {
        echo "Erro ao registrar venda.";
    }
}

}
