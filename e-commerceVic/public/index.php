<?php

$rota = $_GET['url'] ?? '';

switch ($rota) {

    case 'login':
        require_once __DIR__ . '/../app/controllers/AuthController.php';
        (new AuthController())->login();
        break;

    case 'cadastro':
        require_once __DIR__ . '/../app/controllers/AuthController.php';
        (new AuthController())->cadastro();
        break;

    case 'logout':
        require_once __DIR__ . '/../app/controllers/AuthController.php';
        (new AuthController())->logout();
        break;

    case 'produtos':
        require_once __DIR__ . '/../app/controllers/ProdutoController.php';
        (new ProdutoController())->index();
        break;

    case 'admin_produtos':
        require_once __DIR__ . '/../app/controllers/ProdutoController.php';
        (new ProdutoController())->listarAdmin();
        break;

    case 'novo_produto':
        require_once __DIR__ . '/../app/controllers/ProdutoController.php';
        (new ProdutoController())->criar();
        break;

    case 'salvar_produto':
        require_once __DIR__ . '/../app/controllers/ProdutoController.php';
        (new ProdutoController())->salvar();
        break;

    case 'excluir_produto':
        require_once __DIR__ . '/../app/controllers/ProdutoController.php';
        (new ProdutoController())->excluir();
        break;

    case 'minhas_compras':
        require_once __DIR__ . '/../app/controllers/VendaController.php';
        (new VendaController())->minhasCompras();
        break;

    case 'admin_vendas':
        require_once __DIR__ . '/../app/controllers/VendaController.php';
        (new VendaController())->relatorioAdmin();
        break;
        
    case 'simular_compra':
        require_once __DIR__ . '/../app/controllers/VendaController.php';
        (new VendaController())->simularCompra();
        break;

    case 'finalizar_compra':
        require_once __DIR__ . '/../app/controllers/VendaController.php';
        (new VendaController())->finalizarSimulacao();
        break;

    case '':
    default:
        require_once __DIR__ . '/../app/controllers/HomeController.php';
        (new HomeController())->index();
        break;
}