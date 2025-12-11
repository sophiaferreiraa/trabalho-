<?php
require_once __DIR__ . '/ItemCarrinho.php';

class CarrinhoModel {

   public static function iniciar() {
    if (!isset($_SESSION['carrinho'])) {
        $_SESSION['carrinho'] = [];
    } else {
        // Corrige objetos incompletos antigos
        foreach ($_SESSION['carrinho'] as $key => $item) {
            if (is_object($item) && get_class($item) === '__PHP_Incomplete_Class') {
                unset($_SESSION['carrinho'][$key]);
            }
        }
    }
}

    public static function adicionarItem(ItemCarrinho $item) {
        self::iniciar();
        $id = $item->getId();

        if (isset($_SESSION['carrinho'][$id])) {
            $_SESSION['carrinho'][$id]['qtd'] += $item->getQtd();
        } else {
            $_SESSION['carrinho'][$id] = [
                'id' => $item->getId(),
                'qtd' => $item->getQtd(),
                'preco' => $item->getPreco()
            ];
        }
    }

    public static function removerItem($id) {
        self::iniciar();
        unset($_SESSION['carrinho'][$id]);
    }

    public static function alterarQuantidade($id, $novaQtd) {
        self::iniciar();
        if (isset($_SESSION['carrinho'][$id])) {
            $_SESSION['carrinho'][$id]['qtd'] = $novaQtd;
        }
    }

    public static function getCarrinho() {
        self::iniciar();
        $carrinho = [];
        foreach ($_SESSION['carrinho'] as $itemArray) {
            $carrinho[] = new ItemCarrinho(
                $itemArray['id'],
                $itemArray['qtd'],
                $itemArray['preco']
            );
        }
        return $carrinho;
    }

    public static function limpar() {
        unset($_SESSION['carrinho']);
    }
}
