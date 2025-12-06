<?php
// admin_vendas.php (Na raiz)
require_once 'controllers/VendaController.php';

$controller = new VendaController();
$controller->relatorioVendas();
?>