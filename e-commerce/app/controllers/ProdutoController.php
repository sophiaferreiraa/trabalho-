<?php
require_once __DIR__ . '/../dao/ProdutoDAO.php';
require_once __DIR__ . '/../models/ProdutoModel.php';
require_once __DIR__ . '/../helpers/AuthMiddleWare.php';

class ProdutoController {
    private $produtoDAO;

    public function __construct() {
        $this->produtoDAO = new ProdutoDAO();
    }

    public function index() {
        $categoriaId   = $_GET['categoria'] ?? null; 
        $hairTypeId    = $_GET['hair_type'] ?? null;
        $curlPatternId = $_GET['curvatura'] ?? null;
        $productTypeId = $categoriaId; 
        $treatmentId   = $_GET['treatment'] ?? null;
        $brandId       = $_GET['brand'] ?? null;
        $preco         = $_GET['preco'] ?? null;

        $categorias   = $this->produtoDAO->listarProductTypes();
        $hairTypes    = $this->produtoDAO->listarHairTypes();
        $curlPatterns = $this->produtoDAO->listarCurlPatterns();
        $productTypes = $this->produtoDAO->listarProductTypes();
        $treatments   = $this->produtoDAO->listarTreatments();
        $brands       = $this->produtoDAO->listarBrands();

        $produtos = $this->produtoDAO->filtrarProdutos(
            $hairTypeId,
            $curlPatternId,
            $productTypeId,  
            $treatmentId,
            $brandId,
            $preco
        );

        require __DIR__ . '/../views/home/produtos.php';
    }

    public function listarAdmin() {
        AuthMiddleware::requireAdmin();
        $produtos = $this->produtoDAO->listarTodos();
        require __DIR__ . '/../views/admin/produtosCadastrados.php';
    }

    public function criar() {
        AuthMiddleware::requireAdmin();
        $categorias = $this->produtoDAO->listarCategorias();
        require __DIR__ . '/../views/admin/cadastroProduto.php';
    }

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
