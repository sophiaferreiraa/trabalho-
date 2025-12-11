<?php
require_once __DIR__ . '/../dao/ProdutoDAO.php';
require_once __DIR__ . '/../models/ProdutoModel.php';
require_once __DIR__ . '/../helpers/AuthMiddleWare.php';

class ProdutoController {

    private $dao;

    public function __construct() {
        $this->dao = new ProdutoDAO();
    }

    public function index() {

        $categoriaId   = $_GET['categoria'] ?? null;
        $hairTypeId    = $_GET['hair_type'] ?? null;
        $curlPatternId = $_GET['curvatura'] ?? null;
        $productTypeId = $_GET['type'] ?? null;
        $treatmentId   = $_GET['treatment'] ?? null;
        $brandId       = $_GET['brand'] ?? null;
        $preco         = $_GET['preco'] ?? null;

        $categorias   = $this->dao->listarProductTypes();
        $hairTypes    = $this->dao->listarHairTypes();
        $curlPatterns = $this->dao->listarCurlPatterns();
        $productTypes = $this->dao->listarProductTypes();
        $treatments   = $this->dao->listarTreatments();
        $brands       = $this->dao->listarBrands();

        $produtos = $this->dao->filtrarProdutos(
            $hairTypeId,
            $curlPatternId,
            $productTypeId,
            $treatmentId,
            $brandId,
            $preco
        );

        require __DIR__ . '/../views/home/produtos.php';
    }
}
