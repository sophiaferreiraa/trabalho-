<?php
require_once __DIR__ . '/../dao/ProdutoDAO.php';
require_once __DIR__ . '/../models/ProdutoModel.php';
require_once __DIR__ . '/../helpers/AuthMiddleWare.php';

class ProdutoController {
    private $produtoDAO;

    public function __construct() {
        $this->produtoDAO = new ProdutoDAO();
    }

    // Exibe a galeria de produtos para o cliente (com filtro)
    public function index() {
        $categoriaId = $_GET['categoria'] ?? null;
        $categorias = $this->produtoDAO->listarCategorias();

        if ($categoriaId) {
            $produtos = $this->produtoDAO->listarPorCategoria($categoriaId);
        } else {
            $produtos = $this->produtoDAO->listarTodos();
        }
       require __DIR__ . '/../views/home/produtos.php';
    }

    // Lista produtos para o Admin gerenciar
    public function listarAdmin() {
        AuthMiddleware::requireAdmin();
        $produtos = $this->produtoDAO->listarTodos();
        require __DIR__ . '/../views/admin/produtosCadastrados.php';
    }

    // Exibe formulário de cadastro
    public function criar() {
        AuthMiddleware::requireAdmin();
        $categorias = $this->produtoDAO->listarCategorias();
        require __DIR__ . '/../views/admin/cadastroProduto.php';
    }

    // Processa o salvamento do produto
    public function salvar() {
        AuthMiddleware::requireAdmin();

        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $produto = new ProdutoModel();
            $produto->setNome($_POST['nome']);
            $produto->setDescricao($_POST['descricao']);
            $produto->setPreco($_POST['preco']);
            $produto->setEstoque($_POST['estoque']);
            $produto->setCategoriaId($_POST['categoria_id']);

            if ($this->produtoDAO->cadastrar($produto)) {
                header('Location: /e-commerce/public/index.php?url=admin_produtos');
            } else {
                echo "Erro ao cadastrar produto.";
            }
        }
    }

    // Processa a exclusão
    public function excluir() {
        AuthMiddleware::requireAdmin();
        $id = $_GET['id'] ?? null;
        
        if ($id && $this->produtoDAO->excluir($id)) {
            header('Location: /e-commerce/public/index.php?url=admin_produtos');
        } else {
            echo "Erro ao excluir.";
        }
    }
}