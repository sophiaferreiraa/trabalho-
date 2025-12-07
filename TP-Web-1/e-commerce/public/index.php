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

    case '':
        require_once __DIR__ . '/../app/controllers/HomeController.php';
        (new HomeController())->index();
        break;

    case 'logout':
        require_once __DIR__ . '/../app/controllers/AuthController.php';
        (new AuthController())->logout();
        break;

    default:
        echo "Página não encontrada.";
}
